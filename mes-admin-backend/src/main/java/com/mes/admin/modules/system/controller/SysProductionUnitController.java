package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.system.entity.SysProductionUnit;
import com.mes.admin.modules.system.service.SysProductionUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生产单元管理
 */
@RestController
@RequestMapping("/system/production-unit")
public class SysProductionUnitController {

    @Autowired
    private SysProductionUnitService productionUnitService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    public Result<List<SysProductionUnit>> list(
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String unitName,
            @RequestParam(required = false) String unitType,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> params = new HashMap<>();
        if (unitCode != null && !unitCode.isEmpty()) params.put("unitCode", unitCode);
        if (unitName != null && !unitName.isEmpty()) params.put("unitName", unitName);
        if (unitType != null && !unitType.isEmpty()) params.put("unitType", unitType);
        if (orgId != null) params.put("orgId", orgId);
        if (status != null) params.put("status", status);
        return Result.success(productionUnitService.list(params));
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<SysProductionUnit> getById(@PathVariable Long id) {
        return Result.success(productionUnitService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<SysProductionUnit> create(@RequestBody SysProductionUnit unit) {
        return Result.success(productionUnitService.create(unit));
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<SysProductionUnit> update(@RequestBody SysProductionUnit unit) {
        return Result.success(productionUnitService.update(unit));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productionUnitService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        productionUnitService.deleteBatch(ids);
        return Result.success();
    }
}
