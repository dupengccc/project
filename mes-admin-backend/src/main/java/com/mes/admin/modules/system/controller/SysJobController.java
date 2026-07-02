package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.system.entity.SysJob;
import com.mes.admin.modules.system.entity.SysJobLog;
import com.mes.admin.modules.system.service.SysJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务调度管理
 */
@RestController
@RequestMapping("/system/job")
public class SysJobController {

    @Autowired
    private SysJobService jobService;

    /** 查询任务列表（分页） */
    @GetMapping("/list")
    public Result<PageUtil.PageResult<SysJob>> list(
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) String jobGroup,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (jobName != null && !jobName.isEmpty()) params.put("jobName", jobName);
        if (jobGroup != null && !jobGroup.isEmpty()) params.put("jobGroup", jobGroup);
        if (status != null) params.put("status", status);
        List<SysJob> list = jobService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    /** 查询任务详情 */
    @GetMapping("/{id}")
    public Result<SysJob> getById(@PathVariable Long id) {
        return Result.success(jobService.getById(id));
    }

    /** 新增任务 */
    @PostMapping
    public Result<SysJob> create(@RequestBody SysJob job) {
        return Result.success(jobService.create(job));
    }

    /** 修改任务 */
    @PutMapping
    public Result<SysJob> update(@RequestBody SysJob job) {
        return Result.success(jobService.update(job));
    }

    /** 删除任务 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        jobService.delete(id);
        return Result.success();
    }

    /** 批量删除任务 */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        jobService.deleteBatch(ids);
        return Result.success();
    }

    /** 修改任务状态（启动/暂停） */
    @PutMapping("/change-status")
    public Result<Void> changeStatus(@RequestParam Long id, @RequestParam Integer status) {
        jobService.changeStatus(id, status);
        return Result.success();
    }

    /** 立即执行一次 */
    @PutMapping("/run")
    public Result<Void> run(@RequestParam Long id) {
        jobService.run(id);
        return Result.success();
    }

    // ==================== 任务日志 ====================

    /** 查询任务日志列表（分页） */
    @GetMapping("/log/list")
    public Result<PageUtil.PageResult<SysJobLog>> listLog(
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (jobName != null && !jobName.isEmpty()) params.put("jobName", jobName);
        if (status != null) params.put("status", status);
        List<SysJobLog> list = jobService.listLog(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    /** 删除任务日志 */
    @DeleteMapping("/log/{id}")
    public Result<Void> deleteLog(@PathVariable Long id) {
        jobService.deleteLog(id);
        return Result.success();
    }

    /** 清空任务日志 */
    @DeleteMapping("/log/clean")
    public Result<Void> cleanLog() {
        jobService.cleanLog();
        return Result.success();
    }
}
