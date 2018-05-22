package com.haoyunhu.tools.rest.cache;

import com.haoyunhu.tools.rest.bean.RestControllerBeanProperties;
import com.haoyunhu.tools.rest.bean.RestServiceInfo;
import com.haoyunhu.tools.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * service配置数据缓存
 */
public class RestServiceManager {

    private static Logger logger = Logger.getLogger(RestServiceManager.class);

    private static RestServiceManager restServiceManager = null;

    //Rest配置类
    private RestControllerBeanProperties properties = new RestControllerBeanProperties();

    private Map<String, RestServiceInfo> cacheMap = new ConcurrentHashMap<>();

    private static final String SERVICE_NOTE = "service";
    private static final String SERVICE_ATTRIBUTE_ID = "id";
    private static final String SERVICE_ATTRIBUTE_NAME = "name";
    private static final String SERVICE_ATTRIBUTE_METHOD = "method";
    private static final String SERVICE_ATTRIBUTE_LEVEL = "level";
    private static final String SERVICE_ATTRIBUTE_IDEMPOTENT = "idempotent";
    private static final String SERVICE_ATTRIBUTE_IDEMPOTENTTIMES = "idempotentTimes";

    private RestServiceManager() {

    }

    public static synchronized RestServiceManager getInstance() {
        if (restServiceManager == null) {
            restServiceManager = new RestServiceManager();
        }
        return restServiceManager;
    }

    public RestControllerBeanProperties getProperties() {
        return properties;
    }

    public void setProperties(RestControllerBeanProperties properties) {
        this.properties = properties;
    }

    public Map<String, RestServiceInfo> getCacheMap() {
        return cacheMap;
    }

    public static RestServiceInfo get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }

        RestServiceManager instance = getInstance();
        if (instance == null) {
            return null;
        }
        return instance.getCacheMap().get(key);
    }

    //读取CONFIG相关配置
    public void reloadProperties(String filePath) {
        InputStreamReader configStream = null;
        try {
            configStream = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filePath), HttpUtils.UTF8);
            SAXReader reader = new SAXReader();
            Document document = reader.read(configStream);
            Element servicesElement = document.getRootElement();
            List elements = servicesElement.elements(SERVICE_NOTE);
            if (!CollectionUtils.isEmpty(elements)) {
                Map<String, RestServiceInfo> cacheMap = getCacheMap();
                Element element;
                for (Iterator it = elements.iterator(); it.hasNext(); ) {
                    element = (Element) it.next();
                    String id = element.attributeValue(SERVICE_ATTRIBUTE_ID);
                    String name = element.attributeValue(SERVICE_ATTRIBUTE_NAME);
                    String method = element.attributeValue(SERVICE_ATTRIBUTE_METHOD);
                    String levels = element.attributeValue(SERVICE_ATTRIBUTE_LEVEL);
                    String idempotent = element.attributeValue(SERVICE_ATTRIBUTE_IDEMPOTENT);
                    String idempotentTimes = element.attributeValue(SERVICE_ATTRIBUTE_IDEMPOTENTTIMES);
                    if (StringUtils.isBlank(id) || StringUtils.isBlank(name) || StringUtils.isBlank(method) || StringUtils.isBlank(levels)) {
                        continue;
                    }
                    cacheMap.put(id, new RestServiceInfo(id, name, method, StringUtils.split(levels, ","), idempotent, idempotentTimes));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("reload rest xml error.", e);
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.info("reload rest xml error.", e);
        } finally {
            if (configStream != null) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("close configStream error.", e);
                }
            }
        }
    }

}
