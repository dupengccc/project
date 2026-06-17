package com.mes.admin.modules.mes.pro.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.pro.entity.ProWorkorder;
import com.mes.admin.modules.mes.pro.service.ProWorkorderService;
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
@RequestMapping("/api/mes/pro/workorders")
public class ProWorkorderController {

    @Autowired
    private ProWorkorderService proWorkorderService;

    @GetMapping("/list")
    public Result<PageUtil.PageResult<ProWorkorder>> list(
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String workshopName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (orderCode != null) params.put("orderCode", orderCode);
        if (productName != null) params.put("productName", productName);
        if (status != null) params.put("status", status);
        if (workshopName != null) params.put("workshopName", workshopName);
        List<ProWorkorder> list = proWorkorderService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ProWorkorder> getById(@PathVariable Long id) {
        return Result.success(proWorkorderService.getById(id));
    }

    @PostMapping
    public Result<ProWorkorder> create(@RequestBody ProWorkorder entity) {
        return Result.success(proWorkorderService.create(entity));
    }

    @PutMapping
    public Result<ProWorkorder> update(@RequestBody ProWorkorder entity) {
        return Result.success(proWorkorderService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        proWorkorderService.delete(id);
        return Result.success();
    }
}
