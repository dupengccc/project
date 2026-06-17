package com.mes.admin.modules.mes.wm.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.wm.entity.WmLocation;
import com.mes.admin.modules.mes.wm.service.WmLocationService;
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
@RequestMapping("/api/mes/wm/locations")
public class WmLocationController {

    @Autowired
    private WmLocationService wmLocationService;

    @GetMapping
    public Result<PageUtil.PageResult<WmLocation>> list(
            @RequestParam(required = false) String locationCode,
            @RequestParam(required = false) String locationName,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (locationCode != null) params.put("locationCode", locationCode);
        if (locationName != null) params.put("locationName", locationName);
        if (warehouseId != null) params.put("warehouseId", warehouseId);
        if (areaId != null) params.put("areaId", areaId);
        if (status != null) params.put("status", status);
        List<WmLocation> list = wmLocationService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<WmLocation> getById(@PathVariable Long id) {
        return Result.success(wmLocationService.getById(id));
    }

    @PostMapping
    public Result<WmLocation> create(@RequestBody WmLocation entity) {
        return Result.success(wmLocationService.create(entity));
    }

    @PutMapping
    public Result<WmLocation> update(@RequestBody WmLocation entity) {
        return Result.success(wmLocationService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wmLocationService.delete(id);
        return Result.success();
    }
}
