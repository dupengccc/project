package com.mes.admin.modules.mes.qc.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.qc.entity.QcRecord;
import com.mes.admin.modules.mes.qc.service.QcRecordService;
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
@RequestMapping("/api/mes/qc/records")
public class QcRecordController {

    @Autowired
    private QcRecordService qcRecordService;

    @GetMapping
    public Result<PageUtil.PageResult<QcRecord>> list(
            @RequestParam(required = false) String recordCode,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String checkResult,
            @RequestParam(required = false) String checker,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (recordCode != null) params.put("recordCode", recordCode);
        if (productName != null) params.put("productName", productName);
        if (checkResult != null) params.put("checkResult", checkResult);
        if (checker != null) params.put("checker", checker);
        if (orderCode != null) params.put("orderCode", orderCode);
        if (status != null) params.put("status", status);
        List<QcRecord> list = qcRecordService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<QcRecord> getById(@PathVariable Long id) {
        return Result.success(qcRecordService.getById(id));
    }

    @PostMapping
    public Result<QcRecord> create(@RequestBody QcRecord entity) {
        return Result.success(qcRecordService.create(entity));
    }

    @PutMapping
    public Result<QcRecord> update(@RequestBody QcRecord entity) {
        return Result.success(qcRecordService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        qcRecordService.delete(id);
        return Result.success();
    }
}
