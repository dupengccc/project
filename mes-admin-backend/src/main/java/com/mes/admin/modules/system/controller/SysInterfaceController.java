package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.system.entity.SysInterfaceConfig;
import com.mes.admin.modules.system.entity.SysInterfaceLog;
import com.mes.admin.modules.system.service.SysInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口管理控制器
 */
@RestController
@RequestMapping("/system/interface")
public class SysInterfaceController {

    @Autowired
    private SysInterfaceService interfaceService;

    // ==================== 接口配置 ====================

    /**
     * 查询接口配置列表
     */
    @GetMapping("/config/list")
    public Result<Map<String, Object>> listConfigs(
            @RequestParam(required = false) String interfaceCode,
            @RequestParam(required = false) String interfaceName,
            @RequestParam(required = false) String ownerSystem,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> params = new HashMap<>();
        if (interfaceCode != null) params.put("interfaceCode", interfaceCode);
        if (interfaceName != null) params.put("interfaceName", interfaceName);
        if (ownerSystem != null) params.put("ownerSystem", ownerSystem);
        if (status != null) params.put("status", status);
        List<SysInterfaceConfig> list = interfaceService.listConfigs(params);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", list.size());
        return Result.success(data);
    }

    /**
     * 获取接口配置详情
     */
    @GetMapping("/config/{id}")
    public Result<SysInterfaceConfig> getConfig(@PathVariable Long id) {
        return Result.success(interfaceService.getConfig(id));
    }

    /**
     * 保存接口配置
     */
    @PostMapping("/config")
    public Result<SysInterfaceConfig> saveConfig(@RequestBody SysInterfaceConfig config) {
        return Result.success(interfaceService.saveConfig(config));
    }

    /**
     * 删除接口配置
     */
    @DeleteMapping("/config/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        interfaceService.deleteConfig(id);
        return Result.success();
    }

    /**
     * 启用/停用接口
     */
    @PutMapping("/config/toggle-status")
    public Result<Void> toggleStatus(@RequestBody Map<String, Object> params) {
        Long id = ((Number) params.get("id")).longValue();
        Integer status = ((Number) params.get("status")).intValue();
        interfaceService.toggleStatus(id, status);
        return Result.success();
    }

    // ==================== 接口调用 ====================

    /**
     * 调用接口
     */
    @PostMapping("/call/{interfaceCode}")
    public Result<SysInterfaceLog> callInterface(
            @PathVariable String interfaceCode,
            @RequestBody(required = false) Map<String, Object> params,
            @RequestParam(required = false) String bizNo,
            @RequestParam(required = false) String sourceSystem) {
        SysInterfaceLog log = interfaceService.callInterface(interfaceCode, params, bizNo, sourceSystem);
        return Result.success(log);
    }

    /**
     * 重试接口调用
     */
    @PostMapping("/retry/{logId}")
    public Result<SysInterfaceLog> retryCall(@PathVariable Long logId) {
        SysInterfaceLog log = interfaceService.retryCall(logId);
        return Result.success(log);
    }

    /**
     * 批量重试
     */
    @PostMapping("/retry/batch")
    public Result<Map<String, Object>> batchRetry(@RequestBody List<Long> logIds) {
        List<SysInterfaceLog> results = interfaceService.batchRetry(logIds);
        Map<String, Object> data = new HashMap<>();
        data.put("list", results);
        data.put("total", results.size());
        return Result.success(data);
    }

    // ==================== 调用日志 ====================

    /**
     * 查询调用日志列表
     */
    @GetMapping("/log/list")
    public Result<Map<String, Object>> listLogs(
            @RequestParam(required = false) String interfaceCode,
            @RequestParam(required = false) String interfaceName,
            @RequestParam(required = false) Integer callStatus,
            @RequestParam(required = false) String bizNo,
            @RequestParam(required = false) String sourceSystem) {
        Map<String, Object> params = new HashMap<>();
        if (interfaceCode != null) params.put("interfaceCode", interfaceCode);
        if (interfaceName != null) params.put("interfaceName", interfaceName);
        if (callStatus != null) params.put("callStatus", callStatus);
        if (bizNo != null) params.put("bizNo", bizNo);
        if (sourceSystem != null) params.put("sourceSystem", sourceSystem);
        List<SysInterfaceLog> list = interfaceService.listLogs(params);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", list.size());
        return Result.success(data);
    }

    /**
     * 获取调用日志详情
     */
    @GetMapping("/log/{id}")
    public Result<SysInterfaceLog> getLog(@PathVariable Long id) {
        return Result.success(interfaceService.getLog(id));
    }

    /**
     * 查询异常日志
     */
    @GetMapping("/log/errors")
    public Result<Map<String, Object>> listErrorLogs() {
        List<SysInterfaceLog> list = interfaceService.listErrorLogs();
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", list.size());
        return Result.success(data);
    }

    /**
     * 处理异常日志
     */
    @PostMapping("/log/process")
    public Result<Void> processLog(@RequestBody Map<String, Object> params) {
        Long id = ((Number) params.get("id")).longValue();
        String processor = (String) params.get("processor");
        String remark = (String) params.get("remark");
        interfaceService.processLog(id, processor, remark);
        return Result.success();
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return Result.success(interfaceService.getStatistics(startDate, endDate));
    }
}
