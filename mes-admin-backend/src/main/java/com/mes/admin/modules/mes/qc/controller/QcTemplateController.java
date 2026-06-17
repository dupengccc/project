package com.mes.admin.modules.mes.qc.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.qc.entity.QcTemplate;
import com.mes.admin.modules.mes.qc.service.QcTemplateService;
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
@RequestMapping("/api/mes/qc/templates")
public class QcTemplateController {

    @Autowired
    private QcTemplateService qcTemplateService;

    @GetMapping
    public Result<PageUtil.PageResult<QcTemplate>> list(
            @RequestParam(required = false) String templateCode,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (templateCode != null) params.put("templateCode", templateCode);
        if (templateName != null) params.put("templateName", templateName);
        if (productType != null) params.put("productType", productType);
        if (version != null) params.put("version", version);
        if (status != null) params.put("status", status);
        List<QcTemplate> list = qcTemplateService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<QcTemplate> getById(@PathVariable Long id) {
        return Result.success(qcTemplateService.getById(id));
    }

    @PostMapping
    public Result<QcTemplate> create(@RequestBody QcTemplate entity) {
        return Result.success(qcTemplateService.create(entity));
    }

    @PutMapping
    public Result<QcTemplate> update(@RequestBody QcTemplate entity) {
        return Result.success(qcTemplateService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        qcTemplateService.delete(id);
        return Result.success();
    }
}
