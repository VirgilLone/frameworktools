package com.haoyunhu.tools.utils;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * HTTP工具类
 */
public class HttpUtils {

    public final static String UTF8 = "utf-8";
    public final static String GB2312 = "GB2312";
    public final static String ISO88591 = "ISO-8859-1";
    private final static String FORMAT_JSON = "application/json";
    private final static int CONNECTIONMANAGERTIMEOUT = 10000;
    private final static int SOTIMEOUT = 20000;
    private static Logger logger = Logger.getLogger(HttpUtils.class);

    public static String doGet(String url, String queryString, String userAgent) {
        return doGet(url, queryString, UTF8, true, userAgent);
    }

    /**
     * 执行一个HTTP GET请求，返回请求响应的HTML
     *
     * @param url         请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param charset     字符集
     * @param pretty      是否美化
     * @return 返回请求响应的HTML
     */
    public static String doGet(String url, String queryString, String charset, boolean pretty, String userAgent) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }

        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);
        try {

            if (StringUtils.isNotEmpty(queryString)) {
                method.setQueryString(URIUtil.encodeQuery(queryString));
            }
            if (StringUtils.isNotEmpty(userAgent)) {
                client.getParams().setParameter(HttpMethodParams.USER_AGENT, userAgent);
            }
            client.getParams().setConnectionManagerTimeout(CONNECTIONMANAGERTIMEOUT);
            client.getParams().setSoTimeout(SOTIMEOUT);
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (pretty) {
                        response.append(line).append("\n");
                    } else {
                        response.append(line);
                    }
                }
                reader.close();
            }
        } catch (Exception e) {
            logger.error("do get error! url:" + url + ", queryString : " + queryString, e);
        } finally {
            method.releaseConnection();
        }
        return response.toString();
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url     请求的URL地址
     * @param params  请求的查询参数,可以为null
     * @param charset 字符集
     * @param pretty  是否美化
     * @return 返回请求响应的HTML
     */
    public static String doPost(String url, Map<String, String> params, String charset, boolean pretty, String userAgent) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }

        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        // 设置Http Post数据
        if (params != null) {
            NameValuePair[] nvps = new NameValuePair[params.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                nvps[i] = new NameValuePair(entry.getKey(), params.get(entry.getKey()));
                ++i;
            }
            method.setRequestBody(nvps);
        }
        try {
            if (StringUtils.isNotEmpty(userAgent)) {
                client.getParams().setParameter(HttpMethodParams.USER_AGENT, userAgent);
            }
            client.getParams().setConnectionManagerTimeout(CONNECTIONMANAGERTIMEOUT);
            client.getParams().setSoTimeout(SOTIMEOUT);
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (pretty) {
                        response.append(line).append("\n");
                    } else {
                        response.append(line);
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            logger.error("do post error! url:" + url + ", params : " + params, e);
        } finally {
            method.releaseConnection();
        }
        return response.toString();
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url       请求地址
     * @param json      json参数
     * @param headerMap 头信息，无时传null
     * @return 返回请求响应的HTML
     */
    public static String doPost(String url, String json, Map<String, String> headerMap) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }

        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        try {
            if (url.startsWith("https:")) {
                HostnameVerifier hv = new HostnameVerifier() {
                    public boolean verify(String urlHostName, SSLSession session) {
                        return true;
                    }
                };
                try {
                    trustAllHttpsCertificates();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpsURLConnection.setDefaultHostnameVerifier(hv);
            }
            // 设置Http Post数据
            method.setRequestEntity(new StringRequestEntity(json, FORMAT_JSON, UTF8));
            //头信息
            if (headerMap != null) {
                Set<String> sets = headerMap.keySet();
                for (String item : sets) {
                    method.setRequestHeader(item, headerMap.get(item));
                }
            }
            client.getParams().setConnectionManagerTimeout(CONNECTIONMANAGERTIMEOUT);
            client.getParams().setSoTimeout(SOTIMEOUT);
            method.setDoAuthentication(true);
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), UTF8));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
                reader.close();
            }
        } catch (IOException e) {
            logger.error("do post error! url:" + url + ", json : " + json, e);
        } finally {
            method.releaseConnection();
        }

        return response.toString();
    }

    public static String doPostHttps(String url, String json) {
        HttpURLConnection connection = null;
        DataOutputStream out = null;
        BufferedReader reader = null;
        try {
            if (url.startsWith("https:")) {
                //免证书
                HostnameVerifier hv = new HostnameVerifier() {
                    public boolean verify(String urlHostName, SSLSession session) {
                        return true;
                    }
                };
                try {
                    trustAllHttpsCertificates();
                } catch (Exception e) {
                    logger.error("-----trustAllHttpsCertificates" + e.getMessage());
                }
                HttpsURLConnection.setDefaultHostnameVerifier(hv);
            }
            URL validationUrl = new URL(url);
            connection = (HttpURLConnection) validationUrl.openConnection();
            connection.setRequestMethod("POST");
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Length", String.valueOf(json.length()));
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json");
            // 发送请求参数
            connection.connect();
            out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(json);
            out.flush();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
            String line;
            StringBuffer stringBuffer = new StringBuffer(255);

            synchronized (stringBuffer) {
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(new String(line.getBytes(), "utf-8"));
                }
                return stringBuffer.toString();
            }
        } catch (Exception e) {
            logger.error("do post error! url:" + url + ", json : " + json, e);
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }

        }
    }


    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }

    public static String doPost(String url, String json) {
        return doPost(url, json, null);
    }

    public static String doPost(String url, Map<String, String> params, String userAgent) {
        return doPost(url, params, UTF8, true, userAgent);
    }

    public static String doPost(String url, Map<String, String> params) {
        return doPost(url, params, UTF8, true, "");
    }

    static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
}
