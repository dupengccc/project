package com.mes.admin.modules.mes.dv.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.dv.entity.DvRepair;
import com.mes.admin.modules.mes.dv.service.DvRepairService;
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
@RequestMapping("/api/mes/dv/repairs")
public class DvRepairController {

    @Autowired
    private DvRepairService dvRepairService;

    @GetMapping
    public Result<PageUtil.PageResult<DvRepair>> list(
            @RequestParam(required = false) String repairCode,
            @RequestParam(required = false) String deviceCode,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String repairStatus,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (repairCode != null) params.put("repairCode", repairCode);
        if (deviceCode != null) params.put("deviceCode", deviceCode);
        if (deviceName != null) params.put("deviceName", deviceName);
        if (repairStatus != null) params.put("repairStatus", repairStatus);
        if (status != null) params.put("status", status);
        List<DvRepair> list = dvRepairService.findAll(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<DvRepair> getById(@PathVariable Long id) {
        return Result.success(dvRepairService.findById(id));
    }

    @PostMapping
    public Result<DvRepair> create(@RequestBody DvRepair entity) {
        return Result.success(dvRepairService.create(entity));
    }

    @PutMapping
    public Result<DvRepair> update(@RequestBody DvRepair entity) {
        return Result.success(dvRepairService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dvRepairService.delete(id);
        return Result.success();
    }
}
