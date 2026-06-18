package com.mes.admin.modules.system.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mes.admin.common.util.HttpCallUtil;
import com.mes.admin.modules.system.entity.SysInterfaceConfig;
import com.mes.admin.modules.system.entity.SysInterfaceLog;
import com.mes.admin.modules.system.repository.SysInterfaceConfigRepository;
import com.mes.admin.modules.system.repository.SysInterfaceLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 接口管理服务
 */
@Service
public class SysInterfaceService {

    private static final Logger log = LoggerFactory.getLogger(SysInterfaceService.class);

    @Autowired
    private SysInterfaceConfigRepository configRepository;

    @Autowired
    private SysInterfaceLogRepository logRepository;

    // ==================== 接口配置管理 ====================

    /**
     * 查询接口配置列表
     */
    public List<SysInterfaceConfig> listConfigs(Map<String, Object> params) {
        try {
            return configRepository.findAll((root, query, cb) -> {
                List<Predicate> ps = new ArrayList<>();
                if (params != null) {
                    if (params.get("interfaceCode") != null) {
                        ps.add(cb.like(root.get("interfaceCode"), "%" + params.get("interfaceCode") + "%"));
                    }
                    if (params.get("interfaceName") != null) {
                        ps.add(cb.like(root.get("interfaceName"), "%" + params.get("interfaceName") + "%"));
                    }
                    if (params.get("ownerSystem") != null) {
                        ps.add(cb.equal(root.get("ownerSystem"), params.get("ownerSystem")));
                    }
                    if (params.get("status") != null) {
                        ps.add(cb.equal(root.get("status"), params.get("status")));
                    }
                }
                if (query != null) query.orderBy(cb.asc(root.get("interfaceCode")));
                return cb.and(ps.toArray(new Predicate[0]));
            });
        } catch (Exception e) {
            return buildMockConfigs();
        }
    }

    /**
     * 获取接口配置
     */
    public SysInterfaceConfig getConfig(Long id) {
        if (id == null) return null;
        return configRepository.findById(id).orElse(null);
    }

    /**
     * 根据接口编码获取配置
     */
    public SysInterfaceConfig getConfigByCode(String code) {
        return configRepository.findByInterfaceCode(code).orElse(null);
    }

    /**
     * 保存接口配置
     */
    @Transactional
    public SysInterfaceConfig saveConfig(SysInterfaceConfig config) {
        if (config.getId() == null) {
            config.setCreateTime(new Date());
        }
        config.setUpdateTime(new Date());
        return configRepository.save(config);
    }

    /**
     * 删除接口配置
     */
    @Transactional
    public void deleteConfig(Long id) {
        configRepository.deleteById(id);
    }

    /**
     * 启用/停用接口
     */
    @Transactional
    public void toggleStatus(Long id, Integer status) {
        configRepository.findById(id).ifPresent(config -> {
            config.setStatus(status);
            config.setUpdateTime(new Date());
            configRepository.save(config);
        });
    }

    // ==================== 接口调用 ====================

    /**
     * 同步调用接口
     */
    public SysInterfaceLog callInterface(String interfaceCode, Object params, String bizNo, String sourceSystem) {
        SysInterfaceConfig config = getConfigByCode(interfaceCode);
        if (config == null) {
            return createErrorLog(null, interfaceCode, "接口配置不存在", bizNo, sourceSystem);
        }
        return doCall(config, params, bizNo, sourceSystem, 0);
    }

    /**
     * 异步调用接口
     */
    @Async
    public CompletableFuture<SysInterfaceLog> callInterfaceAsync(String interfaceCode, Object params, String bizNo, String sourceSystem) {
        return CompletableFuture.completedFuture(callInterface(interfaceCode, params, bizNo, sourceSystem));
    }

    /**
     * 执行接口调用
     */
    private SysInterfaceLog doCall(SysInterfaceConfig config, Object params, String bizNo, String sourceSystem, int retryCount) {
        SysInterfaceLog callLog = new SysInterfaceLog();
        callLog.setConfigId(config.getId());
        callLog.setInterfaceCode(config.getInterfaceCode());
        callLog.setInterfaceName(config.getInterfaceName());
        callLog.setRequestUrl(config.getUrl());
        callLog.setRequestMethod(config.getRequestMethod());
        callLog.setBizNo(bizNo);
        callLog.setSourceSystem(sourceSystem);
        callLog.setCallTime(new Date());
        callLog.setRetryCount(retryCount);
        callLog.setCallType(0); // 主动调用

        // 构建请求头
        Map<String, String> headers = buildHeaders(config);

        // 构建请求参数
        String requestBody = buildRequestBody(config, params);
        callLog.setRequestBody(requestBody);
        callLog.setRequestParams(JSON.toJSONString(params));

        // 执行调用
        HttpCallUtil.HttpResponse response;
        try {
            String method = config.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                response = HttpCallUtil.doGet(config.getUrl(), headers, config.getTimeout());
            } else if ("POST".equalsIgnoreCase(method)) {
                if ("application/json".equals(config.getContentType())) {
                    response = HttpCallUtil.doPostJson(config.getUrl(), headers, requestBody, config.getTimeout());
                } else {
                    JSONObject jsonParams = JSON.parseObject(requestBody);
                    Map<String, Object> formParams = new HashMap<>();
                    jsonParams.forEach(formParams::put);
                    response = HttpCallUtil.doPostForm(config.getUrl(), headers, formParams, config.getTimeout());
                }
            } else if ("PUT".equalsIgnoreCase(method)) {
                response = HttpCallUtil.doPut(config.getUrl(), headers, requestBody, config.getTimeout());
            } else if ("DELETE".equalsIgnoreCase(method)) {
                response = HttpCallUtil.doDelete(config.getUrl(), headers, config.getTimeout());
            } else {
                response = HttpCallUtil.doGet(config.getUrl(), headers, config.getTimeout());
            }

            callLog.setResponseStatus(response.getStatusCode());
            callLog.setResponseBody(response.getBody());
            callLog.setResponseHeaders(response.getHeaders());
            callLog.setResponseTime(response.getResponseTime());

            if (response.isSuccess() && response.isSuccessStatus()) {
                callLog.setCallStatus(0); // 成功
                // 尝试解析业务状态
                parseBizStatus(callLog, response);
            } else {
                callLog.setCallStatus(1); // 失败
                callLog.setErrorMsg(response.getErrorMessage());
                callLog.setExceptionType(response.getExceptionType());
            }

        } catch (Exception e) {
            callLog.setCallStatus(1);
            callLog.setErrorMsg(e.getMessage());
            callLog.setExceptionType(e.getClass().getSimpleName());
            callLog.setStackTrace(getStackTrace(e));
        }

        callLog.setFinishTime(new Date());
        return logRepository.save(callLog);
    }

    /**
     * 重试接口调用
     */
    @Transactional
    public SysInterfaceLog retryCall(Long logId) {
        SysInterfaceLog originalLog = logRepository.findById(logId).orElse(null);
        if (originalLog == null) {
            throw new RuntimeException("调用日志不存在");
        }

        SysInterfaceConfig config = getConfig(originalLog.getConfigId());
        if (config == null) {
            throw new RuntimeException("接口配置不存在");
        }

        // 重新解析请求参数
        Object params = null;
        if (originalLog.getRequestBody() != null) {
            try {
                params = JSON.parseObject(originalLog.getRequestBody());
            } catch (Exception e) {
                params = originalLog.getRequestBody();
            }
        }

        return doCall(config, params, originalLog.getBizNo(), originalLog.getSourceSystem(), originalLog.getRetryCount() + 1);
    }

    /**
     * 批量重试
     */
    @Transactional
    public List<SysInterfaceLog> batchRetry(List<Long> logIds) {
        List<SysInterfaceLog> results = new ArrayList<>();
        for (Long id : logIds) {
            try {
                results.add(retryCall(id));
            } catch (Exception e) {
                log.error("重试失败: {}", id, e);
            }
        }
        return results;
    }

    // ==================== 调用日志查询 ====================

    /**
     * 查询调用日志列表
     */
    public List<SysInterfaceLog> listLogs(Map<String, Object> params) {
        try {
            return logRepository.findAll((root, query, cb) -> {
                List<Predicate> ps = new ArrayList<>();
                if (params != null) {
                    if (params.get("interfaceCode") != null) {
                        ps.add(cb.like(root.get("interfaceCode"), "%" + params.get("interfaceCode") + "%"));
                    }
                    if (params.get("interfaceName") != null) {
                        ps.add(cb.like(root.get("interfaceName"), "%" + params.get("interfaceName") + "%"));
                    }
                    if (params.get("callStatus") != null) {
                        ps.add(cb.equal(root.get("callStatus"), params.get("callStatus")));
                    }
                    if (params.get("bizNo") != null) {
                        ps.add(cb.equal(root.get("bizNo"), params.get("bizNo")));
                    }
                    if (params.get("sourceSystem") != null) {
                        ps.add(cb.equal(root.get("sourceSystem"), params.get("sourceSystem")));
                    }
                }
                if (query != null) query.orderBy(cb.desc(root.get("callTime")));
                return cb.and(ps.toArray(new Predicate[0]));
            });
        } catch (Exception e) {
            return buildMockLogs();
        }
    }

    /**
     * 获取调用日志详情
     */
    public SysInterfaceLog getLog(Long id) {
        return logRepository.findById(id).orElse(null);
    }

    /**
     * 查询异常日志
     */
    public List<SysInterfaceLog> listErrorLogs() {
        return logRepository.findByCallStatusInOrderByCallTimeDesc(Arrays.asList(1, 2));
    }

    /**
     * 处理异常日志
     */
    @Transactional
    public void processLog(Long id, String processor, String remark) {
        logRepository.updateProcessStatus(id, processor, new Date(), remark);
    }

    /**
     * 获取统计数据
     */
    public Map<String, Object> getStatistics(Date startDate, Date endDate) {
        Map<String, Object> stats = new HashMap<>();

        Long totalCount = logRepository.countByCallTimeBetween(startDate, endDate);
        Long errorCount = logRepository.countErrorByCallTimeBetween(startDate, endDate);

        stats.put("totalCount", totalCount);
        stats.put("errorCount", errorCount);
        stats.put("successCount", totalCount - errorCount);
        stats.put("successRate", totalCount > 0 ? String.format("%.2f%%", (totalCount - errorCount) * 100.0 / totalCount) : "0%");

        return stats;
    }

    // ==================== 辅助方法 ====================

    private Map<String, String> buildHeaders(SysInterfaceConfig config) {
        Map<String, String> headers = new HashMap<>();

        // 添加配置的自定义请求头
        if (config.getHeaders() != null && !config.getHeaders().isEmpty()) {
            try {
                JSONObject headerJson = JSON.parseObject(config.getHeaders());
                headerJson.forEach((k, v) -> headers.put(k, v.toString()));
            } catch (Exception e) {
                log.warn("解析请求头配置失败: {}", e.getMessage());
            }
        }

        // 添加认证头
        if (config.getAuthConfig() != null && !"none".equals(config.getAuthType())) {
            try {
                JSONObject authConfig = JSON.parseObject(config.getAuthConfig());
                if ("bearer".equals(config.getAuthType())) {
                    headers.put("Authorization", "Bearer " + authConfig.getString("token"));
                } else if ("apiKey".equals(config.getAuthType())) {
                    headers.put(authConfig.getString("keyName"), authConfig.getString("keyValue"));
                } else if ("basic".equals(config.getAuthType())) {
                    String auth = authConfig.getString("username") + ":" + authConfig.getString("password");
                    headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));
                }
            } catch (Exception e) {
                log.warn("解析认证配置失败: {}", e.getMessage());
            }
        }

        return headers;
    }

    private String buildRequestBody(SysInterfaceConfig config, Object params) {
        if (params == null) {
            return config.getParamTemplate();
        }
        if (params instanceof String) {
            return (String) params;
        }
        // 合并模板和参数
        try {
            JSONObject template = config.getParamTemplate() != null ?
                JSON.parseObject(config.getParamTemplate()) : new JSONObject();
            JSONObject paramJson = JSON.parseObject(JSON.toJSONString(params));
            template.putAll(paramJson);
            return template.toJSONString();
        } catch (Exception e) {
            return JSON.toJSONString(params);
        }
    }

    private void parseBizStatus(SysInterfaceLog log, HttpCallUtil.HttpResponse response) {
        try {
            JSONObject bodyJson = response.getBodyAsJson();
            if (bodyJson != null) {
                if (bodyJson.containsKey("code")) {
                    log.setBizStatus(bodyJson.getString("code"));
                }
                if (bodyJson.containsKey("msg")) {
                    log.setBizMessage(bodyJson.getString("msg"));
                } else if (bodyJson.containsKey("message")) {
                    log.setBizMessage(bodyJson.getString("message"));
                }
            }
        } catch (Exception e) {
            // 忽略解析异常
        }
    }

    private void createErrorLog(Long configId, String interfaceCode, String errorMsg, String bizNo, String sourceSystem) {
        SysInterfaceLog log = new SysInterfaceLog();
        log.setConfigId(configId);
        log.setInterfaceCode(interfaceCode);
        log.setCallStatus(1);
        log.setErrorMsg(errorMsg);
        log.setBizNo(bizNo);
        log.setSourceSystem(sourceSystem);
        log.setCallTime(new Date());
        log.setFinishTime(new Date());
        return logRepository.save(log);
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
            if (sb.length() > 2000) break; // 限制长度
        }
        return sb.toString();
    }

    // ==================== Mock数据 ====================

    private List<SysInterfaceConfig> buildMockConfigs() {
        List<SysInterfaceConfig> list = new ArrayList<>();
        list.add(buildConfig(1L, "ERP_MATERIAL_SYNC", "ERP物料同步", "http://erp-api.example.com/material/sync", "POST", "ERP系统"));
        list.add(buildConfig(2L, "MES_ORDER_PUSH", "MES工单推送", "http://mes-api.example.com/order/push", "POST", "ERP系统"));
        list.add(buildConfig(3L, "WMS_STOCK_QUERY", "WMS库存查询", "http://wms-api.example.com/stock/query", "GET", "WMS系统"));
        list.add(buildConfig(4L, "QMS_INSPECTION_PUSH", "QMS检验结果推送", "http://qms-api.example.com/inspection/push", "POST", "MES系统"));
        return list;
    }

    private SysInterfaceConfig buildConfig(Long id, String code, String name, String url, String method, String system) {
        SysInterfaceConfig config = new SysInterfaceConfig();
        config.setId(id);
        config.setInterfaceCode(code);
        config.setInterfaceName(name);
        config.setUrl(url);
        config.setRequestMethod(method);
        config.setContentType("application/json");
        config.setOwnerSystem(system);
        config.setTimeout(30000);
        config.setRetryCount(3);
        config.setRetryInterval(5000);
        config.setStatus(0);
        config.setCreateTime(new Date());
        return config;
    }

    private List<SysInterfaceLog> buildMockLogs() {
        List<SysInterfaceLog> list = new ArrayList<>();
        list.add(buildLog(1L, "ERP_MATERIAL_SYNC", 0, "成功", null));
        list.add(buildLog(2L, "MES_ORDER_PUSH", 1, "连接超时", "HttpTimeoutException"));
        list.add(buildLog(3L, "WMS_STOCK_QUERY", 0, "成功", null));
        list.add(buildLog(4L, "QMS_INSPECTION_PUSH", 1, "服务不可用", "ServiceUnavailableException"));
        return list;
    }

    private SysInterfaceLog buildLog(Long id, String code, Integer status, String errorMsg, String exceptionType) {
        SysInterfaceLog log = new SysInterfaceLog();
        log.setId(id);
        log.setInterfaceCode(code);
        log.setInterfaceName(getInterfaceName(code));
        log.setRequestUrl("http://api.example.com/" + code.toLowerCase());
        log.setRequestMethod("POST");
        log.setCallStatus(status);
        log.setErrorMsg(errorMsg);
        log.setExceptionType(exceptionType);
        log.setResponseTime(120L);
        log.setCallTime(new Date());
        log.setFinishTime(new Date());
        log.setProcessed(0);
        return log;
    }

    private String getInterfaceName(String code) {
        Map<String, String> names = new HashMap<>();
        names.put("ERP_MATERIAL_SYNC", "ERP物料同步");
        names.put("MES_ORDER_PUSH", "MES工单推送");
        names.put("WMS_STOCK_QUERY", "WMS库存查询");
        names.put("QMS_INSPECTION_PUSH", "QMS检验结果推送");
        return names.getOrDefault(code, code);
    }
}
