package com.haoyunhu.tools.rest;

import com.haoyunhu.tools.constant.OperatorConstant;
import com.haoyunhu.tools.constant.TrueFalseConstant;
import com.haoyunhu.tools.crm.CrmCenterManager;
import com.haoyunhu.tools.exception.BizErrorBusinessException;
import com.haoyunhu.tools.exception.BizTokenException;
import com.haoyunhu.tools.exception.BizWarnBusinessException;
import com.haoyunhu.tools.redis.RedisService;
import com.haoyunhu.tools.rest.annotation.RestParam;
import com.haoyunhu.tools.rest.bean.RestControllerBeanProperties;
import com.haoyunhu.tools.rest.bean.RestServiceInfo;
import com.haoyunhu.tools.rest.cache.RestServiceManager;
import com.haoyunhu.tools.rest.cache.RestTokenManager;
import com.haoyunhu.tools.rest.constant.RestBaseConstant;
import com.haoyunhu.tools.rest.dto.RequestBaseDto;
import com.haoyunhu.tools.rest.dto.ResponseBaseDto;
import com.haoyunhu.tools.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * RestController帮助类
 * Created by weijun.hu on 2015/11/24.
 */
public class RestControllerBean implements ApplicationContextAware, Controller {

    //时间
    private static ThreadLocal<StopWatch> stopWatchThreadLocal = new ThreadLocal<StopWatch>();
    //Rest Service工具类
    RestServiceManager restServiceManager = RestServiceManager.getInstance();
    //Rest token缓存类
    RestTokenManager restTokenManager = RestTokenManager.getInstance();
    //日志
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RestControllerBean.class);
    //JSON处理类
    private ObjectMapper objectMapper = JacksonUtils.getInstance();
    //SPRING上下文
    private ApplicationContext applicationContext;

    public RestControllerBeanProperties getProperties() {
        return restServiceManager.getProperties();
    }

    public void setProperties(RestControllerBeanProperties properties) {
        this.restServiceManager.setProperties(properties);
    }

    //初始化
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

        //加载Rest Service接口配置
        if (StringUtils.isNotBlank(getProperties().getRestServicePath())) {
            this.restServiceManager.reloadProperties(getProperties().getRestServicePath());
        }

        if (TrueFalseConstant.TRUE_STRING.equals(getProperties().getIsUseL3Valid())) {
            //初始化Rest token缓存
            restTokenManager.setLife(NumberUtils.toLong(getProperties().getTokenCacheLife(), 600000L));
            restTokenManager.clearCacheTimer();
        }
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 执行业务方法
        String methodId = "";
        ResponseBaseDto dto = null;
        String errorMessage = RestBaseConstant.OPERATOR_SUCCESS;
        String success = OperatorConstant.RETURN_SUCCESS;
        Map<String, Object> requestParams = new HashMap<>();
        String result;
        try {
            //获取参数
            String requestMethod = request.getMethod();
            if (!RestBaseConstant.REQUEST_POST.equals(requestMethod)) {
                throw new BizErrorBusinessException(RestBaseConstant.REQUEST_METHOD_ERROR);
            }

            //读取json信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), HttpUtils.UTF8));
            StringBuffer jsonSB = new StringBuffer();
            String temp;
            while ((temp = reader.readLine()) != null) {
                jsonSB.append(temp);
            }
            reader.close();

            String json = jsonSB.toString();
            if (StringUtils.isBlank(json)) {
                throw new BizErrorBusinessException(RestBaseConstant.REQUEST_PARAM_EMPTY);
            }

            // 验证协议并且获取参数
            requestParams = getRequestParams(json);

            // 判断method是否存在rest配置中
            methodId = MapUtils.getString(requestParams, RestBaseConstant.PARAMS_METHOD);
            RestServiceInfo restServiceInfo = restServiceManager.get(methodId);
            if (restServiceInfo == null) {
                throw new BizErrorBusinessException(RestBaseConstant.OPERATOR_METHOD_FAILURE);
            }

            //验证
            requestParamsValid(requestParams, restServiceInfo);

            // 添加自定义参数
            addDefineParams(requestParams, request);

            Class<?> serviceClass = Class.forName(restServiceInfo.getServiceName());
            Method method = getMethod(serviceClass, restServiceInfo.getServiceMethod());

            // 组织方法参数
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] invokeParams = new Object[parameterTypes.length];
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            if (parameterAnnotations != null && parameterAnnotations.length > 0) {
                for (int i = 0; i < parameterAnnotations.length; i++) {
                    Object value = null;
                    Annotation[] paramAnn = parameterAnnotations[i];
                    // 有注解后的解析
                    if (paramAnn.length > 0) {
                        Annotation annotation = paramAnn[0];
                        if (RestParam.class.isInstance(annotation)) {
                            RestParam restParam = (RestParam) paramAnn[0];
                            value = covertParam(parameterTypes[i], requestParams.get(restParam.value()), restParam);
                        }
                    }
                    // 没有注解的并且是请求dto的解析
                    else {
                        value = org.springframework.beans.BeanUtils.instantiate(parameterTypes[i]);
                        if (value instanceof RequestBaseDto) {
                            value = objectMapper.readValue(objectMapper.writeValueAsString(requestParams), parameterTypes[i]);
                        }
                    }
                    invokeParams[i] = value;
                }
            }

            //幂处理
            RedisService redisServiceUtil = getProperties().getRedisServiceUtil();
            String idempotent = restServiceInfo.getIdempotent();
            if (TrueFalseConstant.TRUE_BOOLEAN_STR.equals(idempotent) && redisServiceUtil != null) {
                String key = generateIdempotentKey(methodId, invokeParams);
                String existStr;
                try {
                    existStr = (String) redisServiceUtil.get(key);
                } catch (Exception e) {
                    logger.error("idempotent -> get redis" + e.getMessage());
                    existStr = null;
                }
                if (StringUtils.isNotBlank(existStr)) {
                    throw new BizErrorBusinessException(RestBaseConstant.REQUEST_LIMIT_IDEMPOTENT_REPEAT);
                } else {
                    Long idempotentTimes = NumberUtils.toLong(restServiceInfo.getIdempotentTimes(), 5L);
                    redisServiceUtil.set(key, TrueFalseConstant.TRUE_STRING, idempotentTimes);
                }
            }

            dto = (ResponseBaseDto) ReflectionUtils.invokeMethod(method, applicationContext.getBean(StringUtils.toFirstLowerCase(serviceClass.getSimpleName())), invokeParams);
        } catch (Exception ex) {
            success = OperatorConstant.RETURN_FAILURE;
            if (ex instanceof BizErrorBusinessException) {
                errorMessage = ex.getMessage();
            } else if (ex instanceof BizWarnBusinessException) {
                errorMessage = ex.getMessage();
                success = OperatorConstant.RETURN_EMPTY;
            } else if (ex instanceof BizTokenException) {
                errorMessage = ex.getMessage();
                success = OperatorConstant.RETURN_FAILURE_TOKEN;
            } else {
                errorMessage = RestBaseConstant.OPERATOR_FAILURE;
                logger.error("methodId : " + methodId + "\n, error params:" + requestParams.toString() + "\n, error message: " + ex.getMessage(), ex);
            }
        } finally {
            if (dto == null) {
                dto = new ResponseBaseDto();
            }

            try {
                dto.setResponseTime(getResponseTime());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                success = OperatorConstant.RETURN_FAILURE;
                errorMessage = RestBaseConstant.GET_RESPONSE_TIME_FAILURE;
            }

            dto.setSuccess(success);
            dto.setMessage(errorMessage);
            result = getObjectContent(dto);

            //print log
            String methodIdContent = "methodId : " + methodId;
            String requestContent = " , request content : " + getObjectContent(requestParams);
            String responseContent = " , response result : " + result;
            logger.info(methodIdContent + "\n" + requestContent + "\n" + responseContent);
        }

        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(result);
        writer.flush();
        writer.close();

        return null;
    }

    protected String getResponseTime() throws ClassNotFoundException {
        Object obj = null;
        if (TrueFalseConstant.TRUE_STRING.equals(getProperties().getIsShowResponseTime())) {
            Object bean = applicationContext.getBean(getProperties().getShowTimeServiceName());
            Method method = ReflectionUtils.findMethod(bean.getClass(), getProperties().getShowTimeServiceMethod());
            obj = ReflectionUtils.invokeMethod(method, bean);
        }

        if (obj != null) {
            return obj.toString();
        }
        return "";
    }

    protected void addDefineParams(Map<String, Object> requestMap, HttpServletRequest request) throws ClassNotFoundException {
        // 默认添加IP
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        requestMap.put(RestBaseConstant.REQUEST_IP, ip);
        String userAgent = request.getHeader(RestBaseConstant.USER_AGENT);
        if (StringUtils.isNotBlank(userAgent)) {
            requestMap.put(RestBaseConstant.USER_AGENT, userAgent);
        }
        //设备信息
        String applicationInfo = request.getHeader(RestBaseConstant.APPLICATION_INFO);
        if (StringUtils.isNotBlank(applicationInfo)) {
            requestMap.put(RestBaseConstant.APPLICATION_INFO, applicationInfo);
        }

        //扩展字段
        if (TrueFalseConstant.TRUE_STRING.equals(getProperties().getIsAddDefineParams())) {
            Object bean = applicationContext.getBean(getProperties().getAddDefineServiceName());
            Class<?>[] classesParams = new Class<?>[2];
            classesParams[0] = Map.class;
            classesParams[1] = HttpServletRequest.class;
            Method method = ReflectionUtils.findMethod(bean.getClass(), getProperties().getAddDefineServiceMethod(), classesParams);

            Object[] invokeParams = new Object[2];
            invokeParams[0] = requestMap;
            invokeParams[1] = request;
            ReflectionUtils.invokeMethod(method, bean, invokeParams);
        }
    }

    private Map<String, Object> getRequestParams(String data) {
        Map<String, Object> requestMap;
        try {
            requestMap = objectMapper.readValue(data, Map.class);
        } catch (Exception e) {
            throw new BizErrorBusinessException(RestBaseConstant.OPERATOR_PARAMS_FAILURE);
        }

        //当传递参数是L3格式，那必须要解析
        if (TrueFalseConstant.TRUE_STRING.equals(getProperties().getIsUseL3Valid()) && requestMap.containsKey(RestBaseConstant.L3_KEY_D)) {
            Map<String, Object> paramsMap = validateL3(MapUtils.getString(requestMap, RestBaseConstant.L3_KEY_D));
            if (paramsMap != null) {
                requestMap.putAll(paramsMap);
            }
            requestMap.remove(RestBaseConstant.L3_KEY_D);
            requestMap.put(RestBaseConstant.VALIDATE_LEVEL, RestBaseConstant.LEVEL_THREE);
        }

        return requestMap;
    }

    protected Map<String, Object> validateL3(String data) {
        if (StringUtils.isBlank(data) || data.length() < 64) {
            throw new BizErrorBusinessException(RestBaseConstant.OPERATOR_PARAMS_L3_ERROR);
        }

        // 前64位是token
        String token = data.substring(0, 64);

        // 根据token先去本地拿，若null，再去认证服务器拿sk
        String key = restTokenManager.get(token);
        if (StringUtils.isBlank(key)) {
            CrmCenterManager instance = CrmCenterManager.getInstance(getProperties().getSsoCenterUrl(), getProperties().getSsoCenterChannel(), getProperties().getSsoCenterType());
            key = instance.getSK(token);
            if (StringUtils.isBlank(key)) {
                throw new BizTokenException(RestBaseConstant.OPERATOR_ERROR_TOKEN);
            }
            restTokenManager.put(token, key);
        }

        // 得到参数
        String params = data.substring(64);
        // AES解密，得到键值对
        try {
            params = AES256Utils.aesDecode(params, key);
            Map<String, Object> map = objectMapper.readValue(params, Map.class);
            map.put(RestBaseConstant.PARAMS_TOKEN, token);
            return map;
        } catch (Exception e) {
            throw new BizErrorBusinessException(RestBaseConstant.OPERATOR_L3_AES_ERROR);
        }
    }

    private void requestParamsValid(Map<String, Object> requestMap, RestServiceInfo restServiceInfo) {
        // 是否启用验证
        if (!TrueFalseConstant.TRUE_STRING.equals(getProperties().getIsUseHttpValid())) {
            return;
        }

        Boolean isVaild = false;
        //L3验证
        if (restServiceInfo.getLevels().contains(RestBaseConstant.LEVEL_THREE)
                && RestBaseConstant.LEVEL_THREE.equals(requestMap.get(RestBaseConstant.VALIDATE_LEVEL))) {
            isVaild = true;
        }
        //L2验证
        else if (restServiceInfo.getLevels().contains(RestBaseConstant.LEVEL_TWO)
                && requestMap.containsKey(RestBaseConstant.PARAMS_SIGN)) {
            validateL2(requestMap);
            isVaild = true;
        }
        //L1验证
        else if (restServiceInfo.getLevels().contains(RestBaseConstant.LEVEL_ONE)) {
            isVaild = true;
        }

        if (!isVaild) {
            throw new BizErrorBusinessException(RestBaseConstant.OPERATOR_HTTP_VALID_FAILURE);
        }
    }

    protected Boolean validateL2(Map<String, Object> requestMap) {
        String appId = (String) requestMap.get(RestBaseConstant.PARAMS_APPID);
        String method = (String) requestMap.get(RestBaseConstant.PARAMS_METHOD);
        String sign = (String) requestMap.get(RestBaseConstant.PARAMS_SIGN);
        String eventTime = (String) requestMap.get(RestBaseConstant.PARAMS_EVENT_TIME);

        // 1.验证系统参数是否存在。
        if (StringUtils.isBlank(appId)) {
            throw new BizErrorBusinessException(RestBaseConstant.EMPTY_APPID);
        }

        if (StringUtils.isBlank(method)) {
            throw new BizErrorBusinessException(RestBaseConstant.EMPTY_METHOD);
        }

        if (StringUtils.isBlank(sign)) {
            throw new BizErrorBusinessException(RestBaseConstant.EMPTY_SIGN);
        }

        if (StringUtils.isBlank(eventTime)) {
            throw new BizErrorBusinessException(RestBaseConstant.EMPTY_EVENT_TIME);
        }
        // 2.验证API是否正确
        if (!appId.equals(getProperties().getAppId())) {
            throw new BizErrorBusinessException(RestBaseConstant.ERROR_SECRET);
        }

        // 3.验证签名是否正确
        String md5 = SecurityUtils.getMd5Sign(requestMap, getProperties().getSecretKey());
        if (!sign.equals(md5)) {
            throw new BizErrorBusinessException(RestBaseConstant.ERROR_SIGN);
        }

        return true;
    }

    private Method getMethod(Class clz, String methodName) {
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            if (ObjectUtils.equals(methodName, method.getName())) {
                return method;
            }
        }
        return null;
    }

    private Object covertParam(Class<?> clz, Object param, RestParam restParam) {
        param = param == null ? "" : param;

        String value = String.valueOf(param);
        if (restParam.required() && StringUtils.isBlank(value)) {
            throw new BizErrorBusinessException(RestBaseConstant.OPERATOR_PARAMS_VALUE_EMPTY.replaceAll("\\{0\\}", restParam.value()));
        }

        try {
            if (clz.getName().equals(Integer.class.getName())) {
                return Integer.valueOf(value);
            } else if (clz.getName().equals(BigDecimal.class.getName())) {
                return new BigDecimal(value);
            } else if (clz.getName().equals(Long.class.getName())) {
                return Long.valueOf(value);
            } else if (clz.getName().equals(Short.class.getName())) {
                return Short.valueOf(value);
            } else if (clz.getName().equals(Double.class.getName())) {
                return Double.valueOf(value);
            } else if (clz.getName().equals(Boolean.class.getName())) {
                return TrueFalseConstant.TRUE_STRING.equals(value);
            }
            return value;
        } catch (Exception e) {
            throw new BizErrorBusinessException(RestBaseConstant.OPERATOR_PARAMS_VALUE_COVERT.replaceAll("\\{0\\}", restParam.value()));
        }
    }

    private String generateIdempotentKey(String methodId, Object[] invokeParams) throws IOException {
        StringBuilder strSB = new StringBuilder("TOOLS_REST_");
        if (invokeParams == null || invokeParams.length <= 0) {
            strSB.append(methodId);
        } else {
            for (Object obj : invokeParams) {
                if (obj instanceof RequestBaseDto) {
                    strSB.append(objectMapper.writeValueAsString(obj));
                } else {
                    strSB.append(obj);
                }
            }
        }
        return strSB.toString();
    }

    private String getObjectContent(Object object) {
        if (object == null) {
            return "";
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("getObjectContent writeValueAsString error." + e.getMessage(), e);
        }
        return "";
    }
}
