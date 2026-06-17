package com.mes.admin.modules.mes.md.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.md.entity.MdRoute;
import com.mes.admin.modules.mes.md.service.MdRouteService;
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
@RequestMapping("/api/mes/md/routes")
public class MdRouteController {

    @Autowired
    private MdRouteService mdRouteService;

    @GetMapping
    public Result<PageUtil.PageResult<MdRoute>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (keyword != null) params.put("keyword", keyword);
        if (status != null) params.put("status", status);
        return Result.success(PageUtil.toPage(mdRouteService.list(params), page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MdRoute> getById(@PathVariable Long id) {
        return Result.success(mdRouteService.getById(id));
    }

    @PostMapping
    public Result<MdRoute> create(@RequestBody MdRoute route) {
        return Result.success(mdRouteService.create(route));
    }

    @PutMapping
    public Result<MdRoute> update(@RequestBody MdRoute route) {
        return Result.success(mdRouteService.update(route));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mdRouteService.delete(id);
        return Result.success();
    }
}
