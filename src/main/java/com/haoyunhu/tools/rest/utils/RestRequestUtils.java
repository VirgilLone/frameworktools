package com.haoyunhu.tools.rest.utils;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.haoyunhu.tools.rest.constant.RestBaseConstant;
import com.haoyunhu.tools.rest.dto.RequestBaseDto;
import com.haoyunhu.tools.utils.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weijun.hu on 2015/12/7.
 */
public class RestRequestUtils {

    //日志
    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RestRequestUtils.class);

    private RestRequestUtils() {

    }

    public static <T> T requestL2RestService(String url, String appId, String secretKey, String method, RequestBaseDto requestBaseDto, Class<T> resultClass) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (requestBaseDto == null) {
            return null;
        }

        if (requestBaseDto.getRequestTime() == null || "null".equals(requestBaseDto.getRequestTime())) {
            requestBaseDto.setRequestTime(DateUtils.getNow(new Date()));
        }


        return requestL2RestService(url, appId, secretKey, method, BeanUtils.describe(requestBaseDto), resultClass);
    }

    public static <T> T requestL2RestService(String url, String appId, String secretKey, String method, Map params, Class<T> resultClass) throws IOException {
        if (MapUtils.isEmpty(params) || StringUtils.isBlank(url) || StringUtils.isBlank(method)) {
            return null;
        }

        //加载全部到新MAP中
        Map requestMap = new HashMap();
        requestMap.putAll(params);

        requestMap.put(RestBaseConstant.PARAMS_APPID, appId);
        requestMap.put(RestBaseConstant.PARAMS_METHOD, method);
        requestMap.put(RestBaseConstant.PARAMS_EVENT_TIME, String.valueOf(new Date().getTime()));
        requestMap.put(RestBaseConstant.PARAMS_SIGN, SecurityUtils.getMd5Sign(requestMap, secretKey));

        ObjectMapper instance = JacksonUtils.getInstance();
        String requestBody = instance.writeValueAsString(requestMap);

        Transaction transaction = Cat.newTransaction("Request", url + "?" + method);
        String resultContent = null;
        long startTime = System.currentTimeMillis();
        try {
            resultContent = HttpUtils.doPost(url, requestBody);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception ex) {
            Cat.getProducer().logError(ex);
            transaction.setStatus(ex);
            resultContent = null;
        } finally {
            transaction.complete();
            long endTime = System.currentTimeMillis();
            //输出LOG
            String requestContent = "\n , request content : " + requestBody;
            String responseContent = "\n , response result : " + resultContent;
            logger.info("requestL2RestService url : " + url + " , use time : " + (endTime - startTime) + requestContent + responseContent);
        }

        if (StringUtils.isBlank(resultContent)) {
            return null;
        }

        if (resultClass.getName().equals(String.class.getName())) {
            return (T) resultContent;
        }

        return instance.readValue(resultContent, resultClass);
    }


}
