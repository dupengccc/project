package com.mes.admin.modules.mes.md.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.md.entity.MdWorkstation;
import com.mes.admin.modules.mes.md.service.MdWorkstationService;
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
@RequestMapping("/api/mes/md/workstations")
public class MdWorkstationController {

    @Autowired
    private MdWorkstationService mdWorkstationService;

    @GetMapping
    public Result<PageUtil.PageResult<MdWorkstation>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long workshopId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (keyword != null) params.put("keyword", keyword);
        if (workshopId != null) params.put("workshopId", workshopId);
        if (status != null) params.put("status", status);
        return Result.success(PageUtil.toPage(mdWorkstationService.list(params), page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MdWorkstation> getById(@PathVariable Long id) {
        return Result.success(mdWorkstationService.getById(id));
    }

    @PostMapping
    public Result<MdWorkstation> create(@RequestBody MdWorkstation workstation) {
        return Result.success(mdWorkstationService.create(workstation));
    }

    @PutMapping
    public Result<MdWorkstation> update(@RequestBody MdWorkstation workstation) {
        return Result.success(mdWorkstationService.update(workstation));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mdWorkstationService.delete(id);
        return Result.success();
    }
}
