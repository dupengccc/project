package com.mes.admin.modules.mes.wm.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.wm.entity.WmIn;
import com.mes.admin.modules.mes.wm.service.WmInService;
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
@RequestMapping("/api/mes/wm/ins")
public class WmInController {

    @Autowired
    private WmInService wmInService;

    @GetMapping
    public Result<PageUtil.PageResult<WmIn>> list(
            @RequestParam(required = false) String inCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (inCode != null) params.put("inCode", inCode);
        if (materialName != null) params.put("materialName", materialName);
        if (vendorName != null) params.put("vendorName", vendorName);
        if (warehouseName != null) params.put("warehouseName", warehouseName);
        if (operator != null) params.put("operator", operator);
        if (status != null) params.put("status", status);
        List<WmIn> list = wmInService.list(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<WmIn> getById(@PathVariable Long id) {
        return Result.success(wmInService.getById(id));
    }

    @PostMapping
    public Result<WmIn> create(@RequestBody WmIn entity) {
        return Result.success(wmInService.create(entity));
    }

    @PutMapping
    public Result<WmIn> update(@RequestBody WmIn entity) {
        return Result.success(wmInService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wmInService.delete(id);
        return Result.success();
    }
}
