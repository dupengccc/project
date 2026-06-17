package com.mes.admin.modules.mes.md.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.md.entity.MdProcess;
import com.mes.admin.modules.mes.md.service.MdProcessService;
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
@RequestMapping("/api/mes/md/processes")
public class MdProcessController {

    @Autowired
    private MdProcessService mdProcessService;

    @GetMapping
    public Result<PageUtil.PageResult<MdProcess>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String processType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (keyword != null) params.put("keyword", keyword);
        if (processType != null) params.put("processType", processType);
        if (status != null) params.put("status", status);
        return Result.success(PageUtil.toPage(mdProcessService.list(params), page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MdProcess> getById(@PathVariable Long id) {
        return Result.success(mdProcessService.getById(id));
    }

    @PostMapping
    public Result<MdProcess> create(@RequestBody MdProcess process) {
        return Result.success(mdProcessService.create(process));
    }

    @PutMapping
    public Result<MdProcess> update(@RequestBody MdProcess process) {
        return Result.success(mdProcessService.update(process));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mdProcessService.delete(id);
        return Result.success();
    }
}
