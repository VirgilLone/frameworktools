package com.haoyunhu.tools.rest2.aspect;

import com.haoyunhu.tools.constant.OperatorConstant;
import com.haoyunhu.tools.constant.TrueFalseConstant;
import com.haoyunhu.tools.crm.CrmCenterManager;
import com.haoyunhu.tools.exception.BizErrorBusinessException;
import com.haoyunhu.tools.exception.BizTokenException;
import com.haoyunhu.tools.rest.bean.RestControllerBeanProperties;
import com.haoyunhu.tools.rest.cache.RestServiceManager;
import com.haoyunhu.tools.rest.cache.RestTokenManager;
import com.haoyunhu.tools.rest.constant.RestBaseConstant;
import com.haoyunhu.tools.rest.dto.ResponseBaseDto;
import com.haoyunhu.tools.rest2.annotation.rest.RestLevel;
import com.haoyunhu.tools.rest2.annotation.rest.RestRequestBody;
import com.haoyunhu.tools.rest2.mapper.utils.MapperUtils;
import com.haoyunhu.tools.rest2.model.RestAspectProperties;
import com.haoyunhu.tools.utils.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口模式拦截
 * Created by weijun.hu on 2016/3/31.
 */
public class RestAspect {

    //Rest token缓存类
    private static RestTokenManager restTokenManager = RestTokenManager.getInstance();
    private Logger logger = LoggerFactory.getLogger(RestAspect.class);
    //JSON处理类
    private ObjectMapper objectMapper = JacksonUtils.getInstance();

    //配置类
    private RestAspectProperties restAspectProperties = new RestAspectProperties();

    public static Boolean validateL2(Map<String, Object> requestMap, String appIdV, String secretKey) {
        String appId = (String) requestMap.get(RestBaseConstant.PARAMS_APPID);
        String sign = (String) requestMap.get(RestBaseConstant.PARAMS_SIGN);
        String eventTime = (String) requestMap.get(RestBaseConstant.PARAMS_EVENT_TIME);

        // 1.验证系统参数是否存在。
        if (StringUtils.isBlank(appId)) {
            throw new BizErrorBusinessException(RestBaseConstant.EMPTY_APPID);
        }

        if (StringUtils.isBlank(sign)) {
            throw new BizErrorBusinessException(RestBaseConstant.EMPTY_SIGN);
        }

        if (StringUtils.isBlank(eventTime)) {
            throw new BizErrorBusinessException(RestBaseConstant.EMPTY_EVENT_TIME);
        }
        // 2.验证API是否正确
        if (!appId.equals(appIdV)) {
            throw new BizErrorBusinessException(RestBaseConstant.ERROR_SECRET);
        }

        // 3.验证签名是否正确
        String md5 = SecurityUtils.getMd5Sign(requestMap, secretKey);
        if (!sign.equals(md5)) {
            throw new BizErrorBusinessException(RestBaseConstant.ERROR_SIGN);
        }

        return true;
    }

    public RestAspectProperties getRestAspectProperties() {
        return restAspectProperties;
    }

    public void setRestAspectProperties(RestAspectProperties restAspectProperties) {
        this.restAspectProperties = restAspectProperties;
        //为了测试接口
        if (restAspectProperties != null && TrueFalseConstant.TRUE_STRING.equals(restAspectProperties.getIsImportToTest())) {
            RestServiceManager.getInstance().setProperties(MapperUtils.mapper(restAspectProperties, RestControllerBeanProperties.class));
        }
    }

    public Object processAspect(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        Map<String, Object> requestMap = null;
        String responseContent = "";
        Object dto;
        try {
            //获取参数,先获取spring request body 获取不到去流拿
            Boolean isHasSpringRequestBody = true;
            requestMap = getSpringRequestBody(method, args);
            //当Spring的默认参数没有时,在判断是否有自定义的RequestBody
            if (requestMap == null && isHasParamsAnnotation(method, RestRequestBody.class)) {
                requestMap = getRestRequestBody();
                isHasSpringRequestBody = false;
            }
            //为了不报空，统一来用空MAP处理
            if (requestMap == null) {
                requestMap = new HashMap<>();
            }

            //协议检查
            validLimitedMethod(method.getAnnotation(RestLevel.class), requestMap);

            // 添加自定义参数
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            addDefineParams(requestMap, request);

            //赋值Rest Request Body
            if (isHasSpringRequestBody) {
                dto = point.proceed();
            } else {
                dto = point.proceed(getRestRequestBodyParams(method, args, requestMap));
            }
            if (dto instanceof ResponseBaseDto) {
                ResponseBaseDto responseBaseDto = (ResponseBaseDto) dto;
                responseBaseDto.setResponseTime(getResponseTime());
                if (StringUtils.isBlank(responseBaseDto.getSuccess())) {
                    responseBaseDto.setSuccess(OperatorConstant.RETURN_SUCCESS);
                }
                if (StringUtils.isBlank(responseBaseDto.getMessage())) {
                    responseBaseDto.setMessage(RestBaseConstant.OPERATOR_SUCCESS);
                }

                responseContent = " , response result : " + getObjectContent(responseBaseDto);
            }
        } catch (Exception ex) {
            if (StringUtils.isBlank(responseContent)) {
                responseContent = " , response error msg : " + ex.getMessage();
            }
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            String urlPath = getUrlPath(method);
            String requestContent = " , request content : " + getObjectContent(requestMap);
            logger.info(urlPath + "\n" + requestContent + "\n" + responseContent);
        }

        return dto;
    }

    private String getResponseTime() throws ClassNotFoundException {
        Object obj = null;
        Object showTimeService = restAspectProperties.getShowTimeService();
        String showTimeServiceMethod = restAspectProperties.getShowTimeServiceMethod();
        if (showTimeService != null && StringUtils.isNotBlank(showTimeServiceMethod)) {
            Method method = ReflectionUtils.findMethod(showTimeService.getClass(), showTimeServiceMethod);
            obj = ReflectionUtils.invokeMethod(method, showTimeService);
        }

        if (obj != null) {
            return obj.toString();
        }
        return DateUtils.dateToString(new Date());
    }

    private String getUrlPath(Method method) {
        RequestMapping map = method.getAnnotation(RequestMapping.class);
        String[] strs = map.value();
        if (strs != null && strs.length > 0) {
            return strs[0];
        }

        return "";
    }

    protected void addDefineParams(Map<String, Object> requestMap, HttpServletRequest request) throws ClassNotFoundException {
        // 默认添加IP
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        //设备信息
        String applicationInfo = request.getHeader(RestBaseConstant.APPLICATION_INFO);
        if (StringUtils.isNotBlank(applicationInfo)) {
            requestMap.put(RestBaseConstant.APPLICATION_INFO, applicationInfo);
        }
        requestMap.put(RestBaseConstant.REQUEST_IP, ip);
        String userAgent = request.getHeader(RestBaseConstant.USER_AGENT);
        if (StringUtils.isNotBlank(userAgent)) {
            requestMap.put(RestBaseConstant.USER_AGENT, userAgent);
        }
        String fromSource = request.getHeader(RestBaseConstant.FROM_SOURCE);
        if (StringUtils.isNotBlank(fromSource)) {
            requestMap.put(RestBaseConstant.FROMSOURCE, fromSource);
        }
        String fromSourceId = request.getHeader(RestBaseConstant.FROM_SOURCE_ID);
        if (StringUtils.isNotBlank(fromSourceId)) {
            requestMap.put(RestBaseConstant.FROMSOURCEID, fromSourceId);
        }
    }

    private Map<String, Object> getSpringRequestBody(Method method, Object[] args) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations != null && parameterAnnotations.length > 0) {
            //多个参数循环
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] paramAnn = parameterAnnotations[i];
                if (paramAnn != null && paramAnn.length > 0) {
                    //参数有多个注解
                    for (int j = 0; j < paramAnn.length; j++) {
                        Annotation annotation = paramAnn[j];
                        if (org.springframework.web.bind.annotation.RequestBody.class.isInstance(annotation)) {
                            if (args[i] == null) {
                                return new HashMap<>();
                            }

                            try {
                                String json;
                                if (args[i] instanceof String) {
                                    json = args[i].toString();
                                } else {
                                    json = objectMapper.writeValueAsString(args[i]);
                                }
                                return objectMapper.readValue(json, Map.class);
                            } catch (IOException e) {
                                logger.error("getRequestBodyContext writeValueAsString error." + e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private Map<String, Object> getRestRequestBody() throws IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //读取json信息
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), HttpUtils.UTF8));
        StringBuffer jsonSB = new StringBuffer();
        String temp;
        while ((temp = reader.readLine()) != null) {
            jsonSB.append(temp);
        }
        reader.close();

        //解析json
        String json = jsonSB.toString();
        Map<String, Object> requestMap;
        if (StringUtils.isNotBlank(json)) {
            try {
                requestMap = objectMapper.readValue(json, Map.class);
            } catch (Exception e) {
                throw new BizErrorBusinessException(RestBaseConstant.OPERATOR_PARAMS_FAILURE);
            }
        } else {
            requestMap = new HashMap<>();
        }

        return requestMap;
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

    private void validLimitedMethod(RestLevel restLevel, Map<String, Object> requestParams) throws IOException, BizErrorBusinessException {
        if (restLevel == null) {
            return;
        }

        Boolean isValidation = false;
        //L1验证
        if (RestLevel.LEVEL_ONE == restLevel.level()) {
            isValidation = true;
        }
        //L2验证
        else if (RestLevel.LEVEL_TWO == restLevel.level()
                && validateL2(requestParams, restAspectProperties.getAppId(), restAspectProperties.getSecretKey())) {
            isValidation = true;
        }
        //L3验证
        else if (RestLevel.LEVEL_THREE == restLevel.level()) {
            Map<String, Object> l3ParamsMap = validateL3((String) requestParams.get(RestBaseConstant.L3_KEY_D));
            if (l3ParamsMap != null) {
                requestParams.putAll(l3ParamsMap);
            }
            isValidation = true;
        }

        if (!isValidation) {
            throw new BizErrorBusinessException(RestBaseConstant.OPERATOR_HTTP_VALID_FAILURE);
        }
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
            String ssoCenterUrl = restAspectProperties.getSsoCenterUrl();
            String ssoCenterChannel = restAspectProperties.getSsoCenterChannel();
            String ssoCenterType = restAspectProperties.getSsoCenterType();
            CrmCenterManager instance = CrmCenterManager.getInstance(ssoCenterUrl, ssoCenterChannel, ssoCenterType);
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

    private Boolean isHasParamsAnnotation(Method method, Class annotationParam) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations != null && parameterAnnotations.length > 0) {
            //多个参数循环
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] paramAnn = parameterAnnotations[i];
                if (paramAnn != null && paramAnn.length > 0) {
                    //参数有多个注解
                    for (int j = 0; j < paramAnn.length; j++) {
                        Annotation annotation = paramAnn[j];
                        if (annotationParam.isInstance(annotation)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    private Object[] getRestRequestBodyParams(Method method, Object[] args, Map<String, Object> requestMap) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations != null && parameterAnnotations.length > 0) {
            //多个参数循环
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] paramAnn = parameterAnnotations[i];
                if (paramAnn != null && paramAnn.length > 0) {
                    //参数有多个注解
                    for (int j = 0; j < paramAnn.length; j++) {
                        Annotation annotation = paramAnn[j];
                        if (RestRequestBody.class.isInstance(annotation)) {
                            try {
                                args[i] = objectMapper.readValue(objectMapper.writeValueAsString(requestMap), args[i].getClass());
                            } catch (IOException e) {
                                logger.error("getRestRequestBodyParams writeValueAsString error." + e.getMessage(), e);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return args;
    }
}
