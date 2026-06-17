package com.mes.admin.modules.mes.wm.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.wm.entity.WmOut;
import com.mes.admin.modules.mes.wm.service.WmOutService;
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
@RequestMapping("/api/mes/wm/outs")
public class WmOutController {

    @Autowired
    private WmOutService wmOutService;

    @GetMapping
    public Result<PageUtil.PageResult<WmOut>> list(
            @RequestParam(required = false) String outCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String receiveDept,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (outCode != null) params.put("outCode", outCode);
        if (materialName != null) params.put("materialName", materialName);
        if (receiveDept != null) params.put("receiveDept", receiveDept);
        if (warehouseName != null) params.put("warehouseName", warehouseName);
        if (operator != null) params.put("operator", operator);
        if (status != null) params.put("status", status);
        List<WmOut> list = wmOutService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<WmOut> getById(@PathVariable Long id) {
        return Result.success(wmOutService.getById(id));
    }

    @PostMapping
    public Result<WmOut> create(@RequestBody WmOut entity) {
        return Result.success(wmOutService.create(entity));
    }

    @PutMapping
    public Result<WmOut> update(@RequestBody WmOut entity) {
        return Result.success(wmOutService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wmOutService.delete(id);
        return Result.success();
    }
}
