package com.haoyunhu.tools.utils;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by weijun.hu on 2016/4/12.
 */
public class RestUtils {
    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RestUtils.class);
    private static Map<String, String> cacheMap = new ConcurrentHashMap<>();

    private RestTemplate restTemplate;

    //JSON处理类
    private ObjectMapper objectMapper = JacksonUtils.getInstance();


    public <T> T request(String restUrl, String restApi, Object requestModel, Class<T> resultClass) {
        return request(restUrl, restApi, requestModel, null, resultClass);
    }

    public <T> T request(String restUrl, String restApi, Object requestModel, Map<String, String> headerMap, Class<T> resultClass) {
        if (StringUtils.isBlank(restUrl) || StringUtils.isBlank(restApi)) {
            return null;
        }

        String fromSourceId = UUID.randomUUID().toString();
        if (headerMap == null) {
            headerMap = new HashMap<>();
        }
        //读取配置中的别名作为调用方
        String fromSource = cacheMap.get("runtime.systemAlias");
        if (StringUtils.isNotBlank(fromSource)) {
            headerMap.put("from_source", fromSource);
            headerMap.put("from_source_id", fromSourceId);
        }

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        if (MapUtils.isNotEmpty(headerMap)) {
            Set<String> keySet = headerMap.keySet();
            for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                headers.add(key, headerMap.get(key));
            }
        }

        HttpEntity<Object> formEntity = new HttpEntity<>(requestModel, headers);

        String postUrl = restUrl + restApi;
        Transaction transaction = Cat.newTransaction("Request", postUrl);
        T resultContent = null;
        long startTime = System.currentTimeMillis();
        try {
            resultContent = restTemplate.postForObject(postUrl, formEntity, resultClass);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception ex) {
            Cat.getProducer().logError(ex);
            transaction.setStatus(ex);
            logger.error("restClient request url : " + postUrl + ", error message : " + ex.getMessage(), ex);
            resultContent = null;
        } finally {
           transaction.complete();
            long endTime = System.currentTimeMillis();
            //输出LOG
            String requestHeader = "\n request header : " + getObjectContent(headerMap);
            String requestContent = "\n , request content : " + getObjectContent(requestModel);
            String responseContent = "\n , response result : " + getObjectContent(resultContent);
            logger.info("restClient request url : " + postUrl + " , use time : " + (endTime - startTime) + requestHeader + requestContent + responseContent);
        }

        return resultContent;
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
