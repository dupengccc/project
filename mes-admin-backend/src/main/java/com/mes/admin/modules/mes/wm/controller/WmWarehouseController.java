package com.mes.admin.modules.mes.wm.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.wm.entity.WmWarehouse;
import com.mes.admin.modules.mes.wm.service.WmWarehouseService;
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
@RequestMapping("/api/mes/wm/warehouses")
public class WmWarehouseController {

    @Autowired
    private WmWarehouseService wmWarehouseService;

    @GetMapping
    public Result<PageUtil.PageResult<WmWarehouse>> list(
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String warehouseType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (warehouseCode != null) params.put("warehouseCode", warehouseCode);
        if (warehouseName != null) params.put("warehouseName", warehouseName);
        if (warehouseType != null) params.put("warehouseType", warehouseType);
        if (status != null) params.put("status", status);
        List<WmWarehouse> list = wmWarehouseService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<WmWarehouse> getById(@PathVariable Long id) {
        return Result.success(wmWarehouseService.getById(id));
    }

    @PostMapping
    public Result<WmWarehouse> create(@RequestBody WmWarehouse entity) {
        return Result.success(wmWarehouseService.create(entity));
    }

    @PutMapping
    public Result<WmWarehouse> update(@RequestBody WmWarehouse entity) {
        return Result.success(wmWarehouseService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wmWarehouseService.delete(id);
        return Result.success();
    }
}
