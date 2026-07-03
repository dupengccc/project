package com.mes.admin.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map;

/**
 * HTTP 请求工具类
 * <p>
 * 功能包括：
 * - GET/POST/PUT/DELETE 请求
 * - 文件上传下载
 * - 请求头设置
 * - 超时控制
 * - SSL 处理
 * </p>
 *
 * @author MES Admin
 */
@Component
public class HttpUtil {

    private static final int DEFAULT_TIMEOUT = 30000; // 30秒
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000; // 5秒

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ObjectMapper objectMapper;

    // ==================== GET 请求 ====================

    /**
     * GET 请求
     *
     * @param url 请求地址
     * @return 响应字符串
     */
    public static String get(String url) {
        return get(url, null, null);
    }

    /**
     * GET 请求
     *
     * @param url    请求地址
     * @param params 参数 Map
     * @return 响应字符串
     */
    public static String get(String url, Map<String, ?> params) {
        return get(url, params, null);
    }

    /**
     * GET 请求
     *
     * @param url     请求地址
     * @param params  参数 Map
     * @param headers 请求头
     * @return 响应字符串
     */
    public static String get(String url, Map<String, ?> params, Map<String, String> headers) {
        HttpRequest request = new HttpRequest(url, HttpMethod.GET);
        if (params != null && !params.isEmpty()) {
            request.params(params);
        }
        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }
        return request.execute();
    }

    /**
     * GET 请求，返回字节数组
     */
    public static byte[] getForBytes(String url) {
        return getForBytes(url, null, null);
    }

    public static byte[] getForBytes(String url, Map<String, ?> params, Map<String, String> headers) {
        HttpRequest request = new HttpRequest(url, HttpMethod.GET);
        if (params != null && !params.isEmpty()) {
            request.params(params);
        }
        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }
        return request.executeForBytes();
    }

    // ==================== POST 请求 ====================

    /**
     * POST 请求（Form 表单）
     *
     * @param url    请求地址
     * @param params 表单参数
     * @return 响应字符串
     */
    public static String post(String url, Map<String, ?> params) {
        return post(url, params, null);
    }

    /**
     * POST 请求（Form 表单）
     */
    public static String post(String url, Map<String, ?> params, Map<String, String> headers) {
        HttpRequest request = new HttpRequest(url, HttpMethod.POST);
        request.form(params);
        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }
        return request.execute();
    }

    /**
     * POST 请求（JSON Body）
     *
     * @param url  请求地址
     * @param body 请求体对象
     * @return 响应字符串
     */
    public static String postJson(String url, Object body) {
        return postJson(url, body, null);
    }

    /**
     * POST 请求（JSON Body）
     */
    public static String postJson(String url, Object body, Map<String, String> headers) {
        HttpRequest request = new HttpRequest(url, HttpMethod.POST);
        request.json(body);
        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }
        return request.execute();
    }

    // ==================== PUT 请求 ====================

    /**
     * PUT 请求（JSON Body）
     */
    public static String put(String url, Object body) {
        return put(url, body, null);
    }

    public static String put(String url, Object body, Map<String, String> headers) {
        HttpRequest request = new HttpRequest(url, HttpMethod.PUT);
        request.json(body);
        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }
        return request.execute();
    }

    // ==================== DELETE 请求 ====================

    /**
     * DELETE 请求
     */
    public static String delete(String url) {
        return delete(url, null, null);
    }

    public static String delete(String url, Map<String, ?> params, Map<String, String> headers) {
        HttpRequest request = new HttpRequest(url, HttpMethod.DELETE);
        if (params != null && !params.isEmpty()) {
            request.params(params);
        }
        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }
        return request.execute();
    }

    // ==================== 文件上传下载 ====================

    /**
     * 上传文件（multipart/form-data）
     *
     * @param url        上传地址
     * @param fileName   文件名
     * @param fileBytes  文件字节数组
     * @param fieldName  字段名（默认 file）
     * @return 响应字符串
     */
    public static String uploadFile(String url, String fileName, byte[] fileBytes, String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            fieldName = "file";
        }
        // 使用 RestTemplate 的 MultipartBodyBuilder 或直接使用 LinkedMultiValueMap
        return uploadFile(url, fileName, fileBytes, fieldName, null);
    }

    public static String uploadFile(String url, String fileName, byte[] fileBytes, String fieldName,
                                   Map<String, String> headers) {
        try {
            org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
            body.add(fieldName, new org.springframework.http.ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            });

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            if (headers != null) {
                headers.forEach(httpHeaders::set);
            }

            HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, httpHeaders);

            RestTemplate template = createRestTemplate();
            ResponseEntity<String> response = template.postForEntity(url, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 下载文件
     *
     * @param url 下载地址
     * @return 文件字节数组
     */
    public static byte[] downloadFile(String url) {
        return downloadFile(url, null, null);
    }

    public static byte[] downloadFile(String url, Map<String, ?> params, Map<String, String> headers) {
        HttpRequest request = new HttpRequest(url, HttpMethod.GET);
        if (params != null && !params.isEmpty()) {
            request.params(params);
        }
        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }
        return request.executeForBytes();
    }

    // ==================== 工具方法 ====================

    /**
     * 判断 URL 是否可访问
     */
    public static boolean isUrlReachable(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 构建 URL 参数
     */
    public static String buildUrlParams(Map<String, ?> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, ?> entry : params.entrySet()) {
                if (!first) {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                if (entry.getValue() != null) {
                    sb.append("=");
                    sb.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                }
                first = false;
            }
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("URL 参数编码失败", e);
        }
        return sb.toString();
    }

    /**
     * 构建完整 URL
     */
    public static String buildFullUrl(String baseUrl, Map<String, ?> params) {
        if (StringUtil.isEmpty(baseUrl)) {
            return baseUrl;
        }
        String paramsStr = buildUrlParams(params);
        if (StringUtil.isEmpty(paramsStr)) {
            return baseUrl;
        }
        return baseUrl.contains("?") ? baseUrl + "&" + paramsStr : baseUrl + "?" + paramsStr;
    }

    // ==================== 内部类：HTTP 请求构建器 ====================

    /**
     * HTTP 请求构建器
     */
    public static class HttpRequest {
        private String url;
        private HttpMethod method;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, ?> params;
        private Object body;
        private MediaType mediaType;
        private int timeout = DEFAULT_TIMEOUT;
        private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

        public HttpRequest(String url, HttpMethod method) {
            this.url = url;
            this.method = method;
        }

        public HttpRequest headers(Map<String, String> headers) {
            if (headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public HttpRequest header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public HttpRequest params(Map<String, ?> params) {
            this.params = params;
            return this;
        }

        public HttpRequest json(Object body) {
            this.body = body;
            this.mediaType = MediaType.APPLICATION_JSON;
            return this;
        }

        public HttpRequest form(Map<String, ?> data) {
            this.body = data;
            this.mediaType = MediaType.APPLICATION_FORM_URLENCODED;
            return this;
        }

        public HttpRequest timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public HttpRequest connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public String execute() {
            try {
                RestTemplate template = createRestTemplate();

                // 构建请求头
                HttpHeaders httpHeaders = new HttpHeaders();
                headers.forEach(httpHeaders::set);

                // 设置 Content-Type
                if (mediaType != null) {
                    httpHeaders.setContentType(mediaType);
                }

                // 构建请求体
                HttpEntity<Object> requestEntity = null;
                if (body != null) {
                    if (mediaType == MediaType.APPLICATION_JSON) {
                        // JSON 序列化
                        String json = new ObjectMapper().writeValueAsString(body);
                        requestEntity = new HttpEntity<>(json, httpHeaders);
                    } else if (mediaType == MediaType.APPLICATION_FORM_URLENCODED) {
                        // Form 表单
                        org.springframework.util.MultiValueMap<String, String> formData =
                            new org.springframework.util.LinkedMultiValueMap<>();
                        if (body instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, String> data = (Map<String, String>) body;
                            formData.setAll(data);
                        }
                        requestEntity = new HttpEntity<>(formData, httpHeaders);
                    } else {
                        requestEntity = new HttpEntity<>(body, httpHeaders);
                    }
                } else {
                    requestEntity = new HttpEntity<>(httpHeaders);
                }

                // 构建完整 URL
                String fullUrl = buildFullUrl(url, params);

                // 发送请求
                ResponseEntity<String> response;
                switch (method) {
                    case GET:
                        response = template.exchange(fullUrl, HttpMethod.GET, requestEntity, String.class);
                        break;
                    case POST:
                        response = template.postForEntity(fullUrl, requestEntity, String.class);
                        break;
                    case PUT:
                        response = template.exchange(fullUrl, HttpMethod.PUT, requestEntity, String.class);
                        break;
                    case DELETE:
                        response = template.exchange(fullUrl, HttpMethod.DELETE, requestEntity, String.class);
                        break;
                    default:
                        throw new IllegalArgumentException("不支持的 HTTP 方法: " + method);
                }
                return response.getBody();
            } catch (RestClientException | JsonProcessingException e) {
                throw new RuntimeException("HTTP 请求失败: " + e.getMessage(), e);
            }
        }

        public byte[] executeForBytes() {
            try {
                RestTemplate template = createRestTemplate();

                HttpHeaders httpHeaders = new HttpHeaders();
                headers.forEach(httpHeaders::set);

                HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
                String fullUrl = buildFullUrl(url, params);

                ResponseEntity<byte[]> response = template.exchange(
                    fullUrl, HttpMethod.GET, requestEntity, byte[].class);
                return response.getBody();
            } catch (Exception e) {
                throw new RuntimeException("HTTP 请求失败: " + e.getMessage(), e);
            }
        }

        private RestTemplate createRestTemplate() {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(connectTimeout);
            factory.setReadTimeout(timeout);
            return new RestTemplate(factory);
        }
    }

    // ==================== 创建实例方法（用于注入 RestTemplate） ====================

    /**
     * 创建 HTTP 请求构建器
     */
    public static HttpRequest request(String url, HttpMethod method) {
        return new HttpRequest(url, method);
    }

    /**
     * 创建 GET 请求构建器
     */
    public static HttpRequest get(String url) {
        return new HttpRequest(url, HttpMethod.GET);
    }

    /**
     * 创建 POST 请求构建器
     */
    public static HttpRequest post(String url) {
        return new HttpRequest(url, HttpMethod.POST);
    }

    /**
     * 创建 PUT 请求构建器
     */
    public static HttpRequest put(String url) {
        return new HttpRequest(url, HttpMethod.PUT);
    }

    /**
     * 创建 DELETE 请求构建器
     */
    public static HttpRequest delete(String url) {
        return new HttpRequest(url, HttpMethod.DELETE);
    }
}
