package com.mes.admin.modules.mes.pro.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.pro.entity.ProSchedule;
import com.mes.admin.modules.mes.pro.service.ProScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mes/pro/schedules")
public class ProScheduleController {

    @Autowired
    private ProScheduleService proScheduleService;

    @GetMapping("/list")
    public Result<PageUtil.PageResult<ProSchedule>> list(
            @RequestParam(required = false) String scheduleCode,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (scheduleCode != null) params.put("scheduleCode", scheduleCode);
        if (orderCode != null) params.put("orderCode", orderCode);
        if (productName != null) params.put("productName", productName);
        if (status != null) params.put("status", status);
        List<ProSchedule> list = proScheduleService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ProSchedule> getById(@PathVariable Long id) {
        return Result.success(proScheduleService.getById(id));
    }

    @PostMapping
    public Result<ProSchedule> create(@RequestBody ProSchedule entity) {
        return Result.success(proScheduleService.create(entity));
    }

    @PutMapping
    public Result<ProSchedule> update(@RequestBody ProSchedule entity) {
        return Result.success(proScheduleService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        proScheduleService.delete(id);
        return Result.success();
    }
}
