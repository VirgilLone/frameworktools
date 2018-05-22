package com.haoyunhu.tools.rest2.aspect;

import com.haoyunhu.tools.exception.BizErrorBusinessException;
import com.haoyunhu.tools.redis.RedisService;
import com.haoyunhu.tools.rest.constant.RestBaseConstant;
import com.haoyunhu.tools.rest2.annotation.limited.LimitedExtFrequency;
import com.haoyunhu.tools.rest2.annotation.limited.LimitedFrequency;
import com.haoyunhu.tools.rest2.annotation.limited.LimitedIdempotent;
import com.haoyunhu.tools.rest2.constant.ErrorCodeConstant;
import com.haoyunhu.tools.utils.MD5Utils;
import com.haoyunhu.tools.utils.StringUtils;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 限制请求拦截
 * Created by weijun.hu on 2016/3/31.
 */
public class LimitedAspect {

    private static String VALID_IDEMPOTENT_KEY_TITLE = "LGS_TOOLS_LIMITED_IDEMPOTENT_";
    private static String VALID_LIMITED_EXT_METHOD_KEY_TITLE = "LGS_TOOLS_LIMITED_EXT_METHOD_";
    private static String VALID_LIMITED_METHOD_KEY_TITLE = "LGS_TOOLS_LIMITED_METHOD_";
    private Logger logger = LoggerFactory.getLogger(RestAspect.class);
    private ClassPool classPool;

    private RedisService redisServiceUtil;

    {
        classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(getClass()));
    }

    public RedisService getRedisServiceUtil() {
        return redisServiceUtil;
    }

    public void setRedisServiceUtil(RedisService redisServiceUtil) {
        this.redisServiceUtil = redisServiceUtil;
    }

    public Object processAspect(ProceedingJoinPoint point) throws Throwable {
        //业务幂等拦截
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> aClass = point.getTarget().getClass();

        String methodName = method.getName();
        //获取方法的参数名称和值
        Map<String, Object> paramValueMap = getParamValueMap(aClass, method, args);

        //全局方法名限流
        validLimitedMethod(method.getAnnotation(LimitedFrequency.class), method.getName());

        //扩展限流
        validLimitedExtMethod(method.getAnnotation(LimitedExtFrequency.class), method.getName(), paramValueMap);

        //业务幂等
        validIdempotent(method.getAnnotation(LimitedIdempotent.class), methodName, paramValueMap);

        return point.proceed();
    }

    //业务幂等拦截
    private void validIdempotent(LimitedIdempotent limitedIdempotent, String methodName, Map<String, Object> paramValueMap) throws IOException, BizErrorBusinessException {
        if (limitedIdempotent == null || redisServiceUtil == null) {
            return;
        }

        Map<String, Object> keyNameMap = getKeyNames(limitedIdempotent.params(), limitedIdempotent.excludeParams(), paramValueMap);
        String idempotentKey = generateLimitedKey(VALID_IDEMPOTENT_KEY_TITLE, methodName, keyNameMap);
        if (StringUtils.isBlank(idempotentKey)) {
            return;
        }

        Long increment = redisServiceUtil.increment(idempotentKey, 1L);
        if (increment > 1L) {
            String message = limitedIdempotent.message();
            throw new BizErrorBusinessException(StringUtils.isBlank(message) ? RestBaseConstant.REQUEST_LIMIT_IDEMPOTENT_REPEAT : message, ErrorCodeConstant.ERROR_CODE_10006);
        } else if (increment == 1L) {
            //设置过期
            redisServiceUtil.expire(idempotentKey, limitedIdempotent.timeout());
        }
    }

    //扩展限制拦截
    private void validLimitedExtMethod(LimitedExtFrequency limitedExtFrequency, String methodName, Map<String, Object> paramValueMap) throws IOException, BizErrorBusinessException {
        //对方法进行限流
        if (limitedExtFrequency == null || redisServiceUtil == null) {
            return;
        }

        Map<String, Object> keyNameMap = getKeyNames(limitedExtFrequency.params(), limitedExtFrequency.excludeParams(), paramValueMap);
        String limitedMethodKey = generateLimitedKey(VALID_LIMITED_EXT_METHOD_KEY_TITLE, methodName, keyNameMap);
        if (StringUtils.isBlank(limitedMethodKey)) {
            return;
        }

        Long increment = redisServiceUtil.increment(limitedMethodKey, 1L);
        if (increment > limitedExtFrequency.count()) {
            String message = limitedExtFrequency.message();
            throw new BizErrorBusinessException(StringUtils.isBlank(message) ? RestBaseConstant.REQUEST_LIMIT_EXT_METHOD_REPEAT : message, ErrorCodeConstant.ERROR_CODE_10008);
        } else if (increment == 1L) {
            //设置过期
            redisServiceUtil.expire(limitedMethodKey, limitedExtFrequency.timeout());
        }
    }

    //全局方法名限制拦截
    private void validLimitedMethod(LimitedFrequency limitedFrequency, String methodName) throws IOException, BizErrorBusinessException {
        //对方法进行限流
        if (limitedFrequency == null || redisServiceUtil == null) {
            return;
        }

        StringBuilder strSB = new StringBuilder(VALID_LIMITED_METHOD_KEY_TITLE);
        strSB.append(methodName.toUpperCase());
        String limitedMethodKey = strSB.toString();
        if (StringUtils.isBlank(limitedMethodKey)) {
            return;
        }

        Long increment = redisServiceUtil.increment(limitedMethodKey, 1L);
        if (increment > limitedFrequency.count()) {
            String message = limitedFrequency.message();
            throw new BizErrorBusinessException(StringUtils.isBlank(message) ? RestBaseConstant.REQUEST_LIMIT_METHOD_REPEAT : message, ErrorCodeConstant.ERROR_CODE_10007);
        } else if (increment == 1L) {
            //设置过期
            redisServiceUtil.expire(limitedMethodKey, limitedFrequency.timeout());
        }
    }

    private String generateLimitedKey(String redisKeyTitle, String methodName, Map<String, Object> keyNameMap) throws IOException {
        if (keyNameMap == null) {
            return null;
        }

        StringBuilder strSB = new StringBuilder(redisKeyTitle);
        strSB.append(methodName.toUpperCase()).append("_");

        Set<String> strings = keyNameMap.keySet();
        StringBuilder strSB2 = new StringBuilder();
        for (Iterator<String> iterator = strings.iterator(); iterator.hasNext(); ) {
            String next = iterator.next();
            strSB2.append(next).append(keyNameMap.get(next));
        }
        String md5 = MD5Utils.encode(strSB2.toString());
        strSB.append(md5.toUpperCase());

        return strSB.toString();
    }

    //获取该方法的参数名和对应的值
    private Map<String, Object> getParamValueMap(Class clazz, Method method, Object[] args) {
        Map<String, Object> paramValueMap = new HashMap<>();
        try {
            CtClass ctClass = classPool.get(clazz.getName());

            CtClass[] parameterTypes = new CtClass[args.length];
            Class<?>[] parameterTypeList = method.getParameterTypes();
            for (int i = 0; i < parameterTypeList.length; i++) {
                parameterTypes[i] = classPool.get(parameterTypeList[i].getName());
            }
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName(), parameterTypes);
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                logger.warn("can not found localVariableAttribute, methodName:" + method.getName());
                return null;
            }

            int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
            for (int i = 0; i < args.length; i++) {
                Class<?> paramsTypeClass = parameterTypeList[i];
                if (paramsTypeClass.getName().equals(HttpServletRequest.class.getName())
                        || paramsTypeClass.getName().equals(HttpServletResponse.class.getName())) {
                    continue;
                }

                Object arg = args[i];
                //为空或者是基础类型就直接添加
                if (arg == null || isWrapClass(paramsTypeClass)) {
                    String variableName = attr.variableName(i + pos);
                    if (StringUtils.isNotBlank(variableName)) {
                        paramValueMap.put(variableName, args[i]);
                    }
                } else {
                    try {
                        Map<String, Object> describe = BeanUtils.describe(args[i]);
                        if (describe != null) {
                            paramValueMap.putAll(describe);
                        }
                    } catch (Exception e) {
                        logger.error(" BeanUtils.describe error.", e);
                    }
                }
            }
        } catch (NotFoundException e) {
            logger.error("NotFoundException, methodName:" + method.getName(), e);
        }

        return paramValueMap;
    }

    private Boolean isWrapClass(Class clz) {
        Boolean isWrapClass;
        try {
            isWrapClass = ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            isWrapClass = false;
        }

        return isWrapClass || String.class.getName().equals(clz.getName());
    }

    //获取需要过滤的KEY和VALUE
    private Map<String, Object> getKeyNames(String keyNames, String excludeKeyNames, Map<String, Object> paramValueMap) {
        Map<String, Object> keyNameMap = new HashMap<>();

        if (StringUtils.isBlank(keyNames)) {
            keyNameMap.putAll(paramValueMap);
        } else {
            String[] keyNameList = StringUtils.split(keyNames, ",");
            for (String keyName : keyNameList) {
                if (paramValueMap.containsKey(keyName)) {
                    keyNameMap.put(keyName, paramValueMap.get(keyName));
                }
            }
        }

        //排除不需要的
        if (StringUtils.isNotBlank(excludeKeyNames)) {
            String[] excludeKeyNameList = StringUtils.split(excludeKeyNames, ",");
            for (String keyName : excludeKeyNameList) {
                if (keyNameMap.containsKey(keyName)) {
                    keyNameMap.remove(keyName);
                }
            }
        }

        return keyNameMap;
    }
}
