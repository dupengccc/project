package com.mes.admin.modules.mes.pro.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.pro.entity.ProReport;
import com.mes.admin.modules.mes.pro.service.ProReportService;
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
@RequestMapping("/api/mes/pro/reports")
public class ProReportController {

    @Autowired
    private ProReportService proReportService;

    @GetMapping("/list")
    public Result<PageUtil.PageResult<ProReport>> list(
            @RequestParam(required = false) String reportCode,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (reportCode != null) params.put("reportCode", reportCode);
        if (orderCode != null) params.put("orderCode", orderCode);
        if (productName != null) params.put("productName", productName);
        if (operator != null) params.put("operator", operator);
        if (status != null) params.put("status", status);
        List<ProReport> list = proReportService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ProReport> getById(@PathVariable Long id) {
        return Result.success(proReportService.getById(id));
    }

    @PostMapping
    public Result<ProReport> create(@RequestBody ProReport entity) {
        return Result.success(proReportService.create(entity));
    }

    @PutMapping
    public Result<ProReport> update(@RequestBody ProReport entity) {
        return Result.success(proReportService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        proReportService.delete(id);
        return Result.success();
    }
}
