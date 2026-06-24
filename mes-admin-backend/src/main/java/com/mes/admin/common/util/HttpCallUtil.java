package com.mes.admin.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HTTP 调用工具类（带重试和超时控制）
 */
@Component
public class HttpCallUtil {

    private static final Map<String, RestTemplate> TEMPLATE_CACHE = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化默认模板
        TEMPLATE_CACHE.put("default", createTemplate(30000, 5000));
    }

    /**
     * 创建 RestTemplate
     */
    private static RestTemplate createTemplate(int readTimeout, int connectTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return new RestTemplate(factory);
    }

    /**
     * 获取或创建 RestTemplate
     */
    public static RestTemplate getTemplate(int timeout) {
        String key = "timeout_" + timeout;
        RestTemplate template = TEMPLATE_CACHE.get(key);
        if (template == null) {
            synchronized (HttpCallUtil.class) {
                template = TEMPLATE_CACHE.get(key);
                if (template == null) {
                    template = createTemplate(timeout, 5000);
                    TEMPLATE_CACHE.put(key, template);
                }
            }
        }
        return template;
    }

    /**
     * GET 请求
     */
    public static HttpResponse doGet(String url, Map<String, String> headers, int timeout) {
        return doRequest(url, HttpMethod.GET, headers, null, null, timeout);
    }

    /**
     * GET 请求（带参数）
     */
    public static HttpResponse doGet(String url, Map<String, String> headers, Map<String, Object> params, int timeout) {
        String fullUrl = buildUrl(url, params);
        return doRequest(fullUrl, HttpMethod.GET, headers, null, null, timeout);
    }

    /**
     * POST 请求（Form）
     */
    public static HttpResponse doPostForm(String url, Map<String, String> headers, Map<String, Object> params, int timeout) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (headers != null) {
            httpHeaders.putAll(headers);
        }
        org.springframework.util.MultiValueMap<String, String> formData = new org.springframework.util.LinkedMultiValueMap<>();
        if (params != null) {
            formData.setAll(params);
        }
        return doRequest(url, HttpMethod.POST, httpHeaders, null, formData, timeout);
    }

    /**
     * POST 请求（JSON）
     */
    public static HttpResponse doPostJson(String url, Map<String, String> headers, Object body, int timeout) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            httpHeaders.putAll(headers);
        }
        String jsonBody = body instanceof String ? (String) body : JSON.toJSONString(body);
        return doRequest(url, HttpMethod.POST, httpHeaders, jsonBody, null, timeout);
    }

    /**
     * PUT 请求
     */
    public static HttpResponse doPut(String url, Map<String, String> headers, Object body, int timeout) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            httpHeaders.putAll(headers);
        }
        String jsonBody = body instanceof String ? (String) body : JSON.toJSONString(body);
        return doRequest(url, HttpMethod.PUT, httpHeaders, jsonBody, null, timeout);
    }

    /**
     * DELETE 请求
     */
    public static HttpResponse doDelete(String url, Map<String, String> headers, int timeout) {
        return doRequest(url, HttpMethod.DELETE, headers, null, null, timeout);
    }

    /**
     * 通用请求方法
     */
    private static HttpResponse doRequest(String url, HttpMethod method, HttpHeaders headers,
                                          String body, org.springframework.util.MultiValueMap<String, String> formData,
                                          int timeout) {
        HttpResponse response = new HttpResponse();
        long startTime = System.currentTimeMillis();

        try {
            RestTemplate template = getTemplate(timeout);
            HttpEntity<Object> requestEntity;

            if (formData != null) {
                requestEntity = new HttpEntity<>(formData, headers);
            } else if (body != null) {
                requestEntity = new HttpEntity<>(body, headers);
            } else {
                requestEntity = new HttpEntity<>(headers);
            }

            ResponseEntity<String> exchange = template.exchange(url, method, requestEntity, String.class);

            response.setSuccess(true);
            response.setStatusCode(exchange.getStatusCodeValue());
            response.setBody(exchange.getBody());
            response.setHeaders(JSON.toJSONString(exchange.getHeaders()));

        } catch (RestClientException e) {
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
            response.setExceptionType(e.getClass().getSimpleName());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
            response.setExceptionType(e.getClass().getSimpleName());
        }

        response.setResponseTime(System.currentTimeMillis() - startTime);
        return response;
    }

    /**
     * 构建带参数的URL
     */
    private static String buildUrl(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (!url.contains("?")) {
            sb.append("?");
        } else {
            sb.append("&");
        }
        params.forEach((k, v) -> {
            sb.append(k).append("=");
            if (v != null) {
                try {
                    sb.append(java.net.URLEncoder.encode(v.toString(), StandardCharsets.UTF_8));
                } catch (Exception e) {
                    sb.append(v);
                }
            }
            sb.append("&");
        });
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 添加 Basic 认证头
     */
    public static Map<String, String> addBasicAuth(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
        return java.util.Collections.singletonMap("Authorization", authHeader);
    }

    /**
     * 添加 Bearer Token 认证头
     */
    public static Map<String, String> addBearerToken(String token) {
        return java.util.Collections.singletonMap("Authorization", "Bearer " + token);
    }

    /**
     * 添加 API Key 认证头
     */
    public static Map<String, String> addApiKey(String key, String value) {
        return java.util.Collections.singletonMap(key, value);
    }

    /**
     * HTTP 响应结果封装
     */
    public static class HttpResponse {
        private boolean success;
        private int statusCode;
        private String body;
        private String headers;
        private long responseTime;
        private String errorMessage;
        private String exceptionType;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        public String getHeaders() { return headers; }
        public void setHeaders(String headers) { this.headers = headers; }
        public long getResponseTime() { return responseTime; }
        public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public String getExceptionType() { return exceptionType; }
        public void setExceptionType(String exceptionType) { this.exceptionType = exceptionType; }

        public JSONObject getBodyAsJson() {
            if (body != null && !body.isEmpty()) {
                try {
                    return JSON.parseObject(body);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        public boolean isSuccessStatus() {
            return statusCode >= 200 && statusCode < 300;
        }
    }
}
