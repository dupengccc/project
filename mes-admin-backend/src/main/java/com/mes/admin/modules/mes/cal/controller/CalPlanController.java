package com.mes.admin.modules.mes.cal.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.cal.entity.CalPlan;
import com.mes.admin.modules.mes.cal.service.CalPlanService;
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
@RequestMapping("/api/mes/cal/plans")
public class CalPlanController {

    @Autowired
    private CalPlanService calPlanService;

    @GetMapping
    public Result<PageUtil.PageResult<CalPlan>> list(
            @RequestParam(required = false) String planCode,
            @RequestParam(required = false) String teamName,
            @RequestParam(required = false) String shiftName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (planCode != null) params.put("planCode", planCode);
        if (teamName != null) params.put("teamName", teamName);
        if (shiftName != null) params.put("shiftName", shiftName);
        if (status != null) params.put("status", status);
        List<CalPlan> list = calPlanService.findAll(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<CalPlan> getById(@PathVariable Long id) {
        return Result.success(calPlanService.findById(id));
    }

    @PostMapping
    public Result<CalPlan> create(@RequestBody CalPlan entity) {
        return Result.success(calPlanService.create(entity));
    }

    @PutMapping
    public Result<CalPlan> update(@RequestBody CalPlan entity) {
        return Result.success(calPlanService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        calPlanService.delete(id);
        return Result.success();
    }
}
