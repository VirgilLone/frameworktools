package com.haoyunhu.tools.aspect;


import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by weijun.hu on 2015/12/30.
 */
public class MapperCatAspect {

    private Logger logger = LoggerFactory.getLogger(MapperCatAspect.class);

    public Object processCatAspect(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

        Transaction transaction = Cat.newTransaction("Mapper", methodName);
        try {
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
        }
    }
}
