package com.mes.admin.modules.mes.wm.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.wm.entity.WmArea;
import com.mes.admin.modules.mes.wm.service.WmAreaService;
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
@RequestMapping("/api/mes/wm/areas")
public class WmAreaController {

    @Autowired
    private WmAreaService wmAreaService;

    @GetMapping
    public Result<PageUtil.PageResult<WmArea>> list(
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String areaName,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (areaCode != null) params.put("areaCode", areaCode);
        if (areaName != null) params.put("areaName", areaName);
        if (warehouseId != null) params.put("warehouseId", warehouseId);
        if (warehouseName != null) params.put("warehouseName", warehouseName);
        if (status != null) params.put("status", status);
        List<WmArea> list = wmAreaService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<WmArea> getById(@PathVariable Long id) {
        return Result.success(wmAreaService.getById(id));
    }

    @PostMapping
    public Result<WmArea> create(@RequestBody WmArea entity) {
        return Result.success(wmAreaService.create(entity));
    }

    @PutMapping
    public Result<WmArea> update(@RequestBody WmArea entity) {
        return Result.success(wmAreaService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wmAreaService.delete(id);
        return Result.success();
    }
}
