package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.system.entity.SysLoginLog;
import com.mes.admin.modules.system.service.SysLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录日志管理
 */
@RestController
@RequestMapping("/system/login-log")
public class SysLoginLogController {

    @Autowired
    private SysLoginLogService logService;

    /**
     * 列表查询（支持按工号/姓名/IP/状态/时间范围）
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String empNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String loginIp,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("empNo", empNo);
        params.put("name", name);
        params.put("loginIp", loginIp);
        if (status != null) {
            params.put("status", status);
        }
        List<SysLoginLog> all = logService.findAll(params);
        // 内存分页
        int total = all.size();
        int from = Math.max(0, (page - 1) * pageSize);
        int to = Math.min(total, from + pageSize);
        List<SysLoginLog> pageList = from >= total ? new java.util.ArrayList<>() : all.subList(from, to);

        Map<String, Object> data = new HashMap<>();
        data.put("list", pageList);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        return Result.success(data);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<SysLoginLog> detail(@PathVariable Long id) {
        return Result.success(logService.findById(id));
    }

    /**
     * 单条删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        logService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty()) return Result.success();
        for (Long id : ids) {
            logService.delete(id);
        }
        return Result.success();
    }

    /**
     * 清理指定天数前的日志
     */
    @PostMapping("/clean")
    public Result<Void> clean(@RequestBody Map<String, Integer> body) {
        Integer days = body.get("days");
        if (days == null || days < 0) days = 30;
        logService.cleanOverDays(days);
        return Result.success();
    }
}
