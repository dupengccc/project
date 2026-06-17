package com.mes.admin.modules.mes.wm.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.wm.entity.WmStock;
import com.mes.admin.modules.mes.wm.service.WmStockService;
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
@RequestMapping("/api/mes/wm/stocks")
public class WmStockController {

    @Autowired
    private WmStockService wmStockService;

    @GetMapping
    public Result<PageUtil.PageResult<WmStock>> list(
            @RequestParam(required = false) String materialCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String areaName,
            @RequestParam(required = false) String locationName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (materialCode != null) params.put("materialCode", materialCode);
        if (materialName != null) params.put("materialName", materialName);
        if (warehouseName != null) params.put("warehouseName", warehouseName);
        if (areaName != null) params.put("areaName", areaName);
        if (locationName != null) params.put("locationName", locationName);
        if (status != null) params.put("status", status);
        List<WmStock> list = wmStockService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<WmStock> getById(@PathVariable Long id) {
        return Result.success(wmStockService.getById(id));
    }

    @PostMapping
    public Result<WmStock> create(@RequestBody WmStock entity) {
        return Result.success(wmStockService.create(entity));
    }

    @PutMapping
    public Result<WmStock> update(@RequestBody WmStock entity) {
        return Result.success(wmStockService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wmStockService.delete(id);
        return Result.success();
    }
}
