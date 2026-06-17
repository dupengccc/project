package com.mes.admin.modules.mes.qc.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.qc.entity.QcDefect;
import com.mes.admin.modules.mes.qc.service.QcDefectService;
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
@RequestMapping("/api/mes/qc/defects")
public class QcDefectController {

    @Autowired
    private QcDefectService qcDefectService;

    @GetMapping
    public Result<PageUtil.PageResult<QcDefect>> list(
            @RequestParam(required = false) String defectCode,
            @RequestParam(required = false) String defectName,
            @RequestParam(required = false) String defectType,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (defectCode != null) params.put("defectCode", defectCode);
        if (defectName != null) params.put("defectName", defectName);
        if (defectType != null) params.put("defectType", defectType);
        if (severity != null) params.put("severity", severity);
        if (status != null) params.put("status", status);
        List<QcDefect> list = qcDefectService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<QcDefect> getById(@PathVariable Long id) {
        return Result.success(qcDefectService.getById(id));
    }

    @PostMapping
    public Result<QcDefect> create(@RequestBody QcDefect entity) {
        return Result.success(qcDefectService.create(entity));
    }

    @PutMapping
    public Result<QcDefect> update(@RequestBody QcDefect entity) {
        return Result.success(qcDefectService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        qcDefectService.delete(id);
        return Result.success();
    }
}
