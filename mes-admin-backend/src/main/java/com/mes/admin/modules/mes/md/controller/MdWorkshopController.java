package com.mes.admin.modules.mes.md.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.md.entity.MdWorkshop;
import com.mes.admin.modules.mes.md.service.MdWorkshopService;
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
@RequestMapping("/api/mes/md/workshops")
public class MdWorkshopController {

    @Autowired
    private MdWorkshopService mdWorkshopService;

    @GetMapping
    public Result<PageUtil.PageResult<MdWorkshop>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (keyword != null) params.put("keyword", keyword);
        if (status != null) params.put("status", status);
        return Result.success(PageUtil.toPage(mdWorkshopService.list(params), page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MdWorkshop> getById(@PathVariable Long id) {
        return Result.success(mdWorkshopService.getById(id));
    }

    @PostMapping
    public Result<MdWorkshop> create(@RequestBody MdWorkshop workshop) {
        return Result.success(mdWorkshopService.create(workshop));
    }

    @PutMapping
    public Result<MdWorkshop> update(@RequestBody MdWorkshop workshop) {
        return Result.success(mdWorkshopService.update(workshop));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mdWorkshopService.delete(id);
        return Result.success();
    }
}
