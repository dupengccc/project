package com.mes.admin.modules.mes.md.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.md.entity.MdVendor;
import com.mes.admin.modules.mes.md.service.MdVendorService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/mes/md/vendors")
public class MdVendorController {

    @Autowired
    private MdVendorService mdVendorService;

    @GetMapping
    public Result<PageUtil.PageResult<MdVendor>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String supplyLevel,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (keyword != null) params.put("keyword", keyword);
        if (supplyLevel != null) params.put("supplyLevel", supplyLevel);
        if (status != null) params.put("status", status);
        return Result.success(PageUtil.toPage(mdVendorService.list(params), page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MdVendor> getById(@PathVariable Long id) {
        return Result.success(mdVendorService.getById(id));
    }

    @PostMapping
    public Result<MdVendor> create(@RequestBody MdVendor vendor) {
        return Result.success(mdVendorService.create(vendor));
    }

    @PutMapping
    public Result<MdVendor> update(@RequestBody MdVendor vendor) {
        return Result.success(mdVendorService.update(vendor));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mdVendorService.delete(id);
        return Result.success();
    }
}
