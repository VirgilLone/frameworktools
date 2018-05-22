package com.haoyunhu.tools.crm;

import com.haoyunhu.tools.crm.dto.CrmLoginRequestDto;
import com.haoyunhu.tools.crm.dto.CrmResponseDto;
import com.haoyunhu.tools.crm.dto.CrmTokenRequestDto;
import com.haoyunhu.tools.utils.HttpUtils;
import com.haoyunhu.tools.utils.JacksonUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

public class CrmCenterManager {
    private static Logger logger = LoggerFactory.getLogger(CrmCenterManager.class);

    private static CrmCenterManager crmCenterManager = null;

    private ObjectMapper objectMapper = JacksonUtils.getInstance();

    private String crmCenterUrl;
    private String crmCenterChannel;
    private String crmCenterType;

    public String getCrmCenterUrl() {
        return crmCenterUrl;
    }

    public void setCrmCenterUrl(String crmCenterUrl) {
        this.crmCenterUrl = crmCenterUrl;
    }

    public String getCrmCenterType() {
        return crmCenterType;
    }

    public void setCrmCenterType(String crmCenterType) {
        this.crmCenterType = crmCenterType;
    }

    public String getCrmCenterChannel() {
        return crmCenterChannel;
    }

    public void setCrmCenterChannel(String crmCenterChannel) {
        this.crmCenterChannel = crmCenterChannel;
    }

    private CrmCenterManager() {

    }

    public static synchronized CrmCenterManager getInstance(String crmCenterUrl, String crmCenterChannel, String crmCenterType) {
        if (crmCenterManager == null) {
            crmCenterManager = new CrmCenterManager();
        }
        crmCenterManager.setCrmCenterUrl(crmCenterUrl);
        crmCenterManager.setCrmCenterChannel(crmCenterChannel);
        crmCenterManager.setCrmCenterType(crmCenterType);
        return crmCenterManager;
    }

    // 去认证接口拿取sk，用于解密AES
    public String getSK(String token) {
        try {
            String result = HttpUtils.doPostHttps(crmCenterUrl, objectMapper.writeValueAsString(new CrmTokenRequestDto(token)));
//            String result = HttpUtils.doPost(crmCenterUrl, objectMapper.writeValueAsString(new CrmTokenRequestDto(token)));
            CrmResponseDto crmResponseDto = objectMapper.readValue(result, CrmResponseDto.class);
            return crmResponseDto.getData().getSk();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(MarkerFactory.getMarker("CrmCenterManager"), e.getMessage(), e);
        }
        return "";
    }

    // 登录
    public CrmResponseDto login(String account, String password) {
        try {
            CrmLoginRequestDto crmLoginRequestDto = new CrmLoginRequestDto();
            crmLoginRequestDto.setAccount(account);
            crmLoginRequestDto.setPassword(password);
            crmLoginRequestDto.setChannel(getCrmCenterChannel());
            crmLoginRequestDto.setType(getCrmCenterType());
            //String result = HttpUtils.doPost(crmCenterUrl, objectMapper.writeValueAsString(crmLoginRequestDto), null);
            //不走认账
            String result = HttpUtils.doPostHttps(crmCenterUrl, objectMapper.writeValueAsString(crmLoginRequestDto));
            return objectMapper.readValue(result, CrmResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(MarkerFactory.getMarker("CrmCenterManager"), e.getMessage(), e);
        }
        return null;
    }

    public static void main(String[] args) {
        //https://sso.zhaogangtest.com/api/ssl
        //crmLoginRequestDto.setChannel("logisticsCarrier");
//        crmLoginRequestDto.setType("W");
        CrmCenterManager centerManager = getInstance("https://sso.zhaoganguat.com/api/ssl", "logisticsCarrier", "W");
        CrmResponseDto dto = centerManager.login("13989650236", "e10adc3949ba59abbe56e057f20f883e");
        System.out.println(dto.toString());
    }
}
