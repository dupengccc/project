package com.mes.admin.modules.mes.tm.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.tm.entity.TmTool;
import com.mes.admin.modules.mes.tm.service.TmToolService;
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
@RequestMapping("/api/mes/tm/tools")
public class TmToolController {

    @Autowired
    private TmToolService tmToolService;

    @GetMapping
    public Result<PageUtil.PageResult<TmTool>> list(
            @RequestParam(required = false) String toolCode,
            @RequestParam(required = false) String toolName,
            @RequestParam(required = false) String toolType,
            @RequestParam(required = false) String workshopName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (toolCode != null) params.put("toolCode", toolCode);
        if (toolName != null) params.put("toolName", toolName);
        if (toolType != null) params.put("toolType", toolType);
        if (workshopName != null) params.put("workshopName", workshopName);
        if (status != null) params.put("status", status);
        List<TmTool> list = tmToolService.findAll(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<TmTool> getById(@PathVariable Long id) {
        return Result.success(tmToolService.findById(id));
    }

    @PostMapping
    public Result<TmTool> create(@RequestBody TmTool entity) {
        return Result.success(tmToolService.create(entity));
    }

    @PutMapping
    public Result<TmTool> update(@RequestBody TmTool entity) {
        return Result.success(tmToolService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tmToolService.delete(id);
        return Result.success();
    }
}
