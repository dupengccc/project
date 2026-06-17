package com.mes.admin.modules.mes.dv.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.dv.entity.DvCheck;
import com.mes.admin.modules.mes.dv.service.DvCheckService;
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
@RequestMapping("/api/mes/dv/checks")
public class DvCheckController {

    @Autowired
    private DvCheckService dvCheckService;

    @GetMapping
    public Result<PageUtil.PageResult<DvCheck>> list(
            @RequestParam(required = false) String checkCode,
            @RequestParam(required = false) String deviceCode,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String checkType,
            @RequestParam(required = false) String checkResult,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (checkCode != null) params.put("checkCode", checkCode);
        if (deviceCode != null) params.put("deviceCode", deviceCode);
        if (deviceName != null) params.put("deviceName", deviceName);
        if (checkType != null) params.put("checkType", checkType);
        if (checkResult != null) params.put("checkResult", checkResult);
        if (status != null) params.put("status", status);
        List<DvCheck> list = dvCheckService.findAll(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<DvCheck> getById(@PathVariable Long id) {
        return Result.success(dvCheckService.findById(id));
    }

    @PostMapping
    public Result<DvCheck> create(@RequestBody DvCheck entity) {
        return Result.success(dvCheckService.create(entity));
    }

    @PutMapping
    public Result<DvCheck> update(@RequestBody DvCheck entity) {
        return Result.success(dvCheckService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dvCheckService.delete(id);
        return Result.success();
    }
}
