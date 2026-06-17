package com.mes.admin.modules.mes.dv.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.dv.entity.DvDevice;
import com.mes.admin.modules.mes.dv.service.DvDeviceService;
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
@RequestMapping("/api/mes/dv/devices")
public class DvDeviceController {

    @Autowired
    private DvDeviceService dvDeviceService;

    @GetMapping
    public Result<PageUtil.PageResult<DvDevice>> list(
            @RequestParam(required = false) String deviceCode,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) Long workshopId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (deviceCode != null) params.put("deviceCode", deviceCode);
        if (deviceName != null) params.put("deviceName", deviceName);
        if (deviceType != null) params.put("deviceType", deviceType);
        if (workshopId != null) params.put("workshopId", workshopId);
        if (status != null) params.put("status", status);
        List<DvDevice> list = dvDeviceService.findAll(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<DvDevice> getById(@PathVariable Long id) {
        return Result.success(dvDeviceService.findById(id));
    }

    @PostMapping
    public Result<DvDevice> create(@RequestBody DvDevice entity) {
        return Result.success(dvDeviceService.create(entity));
    }

    @PutMapping
    public Result<DvDevice> update(@RequestBody DvDevice entity) {
        return Result.success(dvDeviceService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dvDeviceService.delete(id);
        return Result.success();
    }
}
