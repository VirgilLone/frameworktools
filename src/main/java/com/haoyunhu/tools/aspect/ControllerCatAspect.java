package com.haoyunhu.tools.aspect;


import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.haoyunhu.tools.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Created by weijun.hu on 2015/12/30.
 */
public class ControllerCatAspect {

    private Logger logger = LoggerFactory.getLogger(ControllerCatAspect.class);

    public Object processCatAspect(ProceedingJoinPoint point) throws Throwable {
        //监听来源
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String fromSource = request.getHeader("from_source");
        if (StringUtils.isNotBlank(fromSource)) {
            Transaction transaction = Cat.newTransaction("fromSource", fromSource);
            transaction.setStatus(Transaction.SUCCESS);
            transaction.complete();
        }

        String methodPath = "";
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequestMapping map = method.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
        String[] strs = map.value();

        if (strs != null && strs.length > 0) {
            methodPath = strs[0];
        }
        Transaction transaction = Cat.newTransaction("Controller", methodPath);
        try {
            Cat.logMetricForCount(methodPath);
            Object retVal = point.proceed();
            transaction.setStatus(Transaction.SUCCESS);
            return retVal;
        } catch (Exception ex) {

            Cat.getProducer().logError(ex);
            transaction.setStatus(ex);
            logger.error(ex.getMessage(), ex);

            throw ex;
        } finally {
            transaction.complete();

            //监听来源请求服务端详情
            if (StringUtils.isNotBlank(fromSource)) {
                Transaction transaction2 = Cat.newTransaction("fromSourceRequest", fromSource + "->" + methodPath);
                transaction2.setStatus(Transaction.SUCCESS);
                transaction2.complete();
            }
        }
    }
}
