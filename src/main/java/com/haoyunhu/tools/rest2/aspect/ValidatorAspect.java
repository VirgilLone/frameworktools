package com.haoyunhu.tools.rest2.aspect;


import com.haoyunhu.tools.exception.BizErrorBusinessException;
import com.haoyunhu.tools.rest2.annotation.validtor.LogisticsRestNotNull;
import com.haoyunhu.tools.rest2.annotation.validtor.LogisticsRestValid;
import com.haoyunhu.tools.utils.ListUtils;
import com.haoyunhu.tools.utils.StringUtils;
import org.apache.commons.collections.MapUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 验证拦截
 * Created by weijun.hu on 2016/3/31.
 */
public class ValidatorAspect {

    private Logger logger = LoggerFactory.getLogger(ValidatorAspect.class);

    public Object processAspect(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        checkRestValid(method, args);

        return point.proceed();
    }

    private void checkRestValid(Method method, Object[] args) throws BizErrorBusinessException {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations != null && parameterAnnotations.length > 0) {
            //多个参数循环
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] paramAnn = parameterAnnotations[i];
                if (paramAnn != null && paramAnn.length > 0) {
                    //参数有多个注解
                    for (int j = 0; j < paramAnn.length; j++) {
                        Annotation annotation = paramAnn[j];
                        if (LogisticsRestValid.class.isInstance(annotation)) {
                            checkField(args[i]);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void checkField(Object obj) throws BizErrorBusinessException {
        if (obj == null || isWrapClass(obj.getClass())) {
            return;
        }

        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field f : fields) {
            LogisticsRestNotNull logisticsRestNotNull = f.getAnnotation(LogisticsRestNotNull.class);
            if (logisticsRestNotNull != null) {
                try {
                    Method method = aClass.getMethod("get" + StringUtils.toFirstUpperCase(f.getName()));
                    Object value = method.invoke(obj);
                    if (value == null) {
                        throw new BizErrorBusinessException(logisticsRestNotNull.message());
                    }
                    if (value instanceof String) {
                        if (StringUtils.isBlank(value.toString())) {
                            throw new BizErrorBusinessException(logisticsRestNotNull.message());
                        }
                    } else if (value instanceof List) {
                        if (ListUtils.isEmpty((List) value)) {
                            throw new BizErrorBusinessException(logisticsRestNotNull.message());
                        }
                    } else if (value instanceof Map) {
                        if (MapUtils.isEmpty((Map) value)) {
                            throw new BizErrorBusinessException(logisticsRestNotNull.message());
                        }
                    }
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage());
                } catch (InvocationTargetException e) {
                    logger.error(e.getMessage());
                } catch (NoSuchMethodException e) {
                    logger.error(e.getMessage());
                }
            }
        }
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
}
