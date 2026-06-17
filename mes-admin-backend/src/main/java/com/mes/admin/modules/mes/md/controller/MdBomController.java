package com.mes.admin.modules.mes.md.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.md.entity.MdBom;
import com.mes.admin.modules.mes.md.service.MdBomService;
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
@RequestMapping("/api/mes/md/boms")
public class MdBomController {

    @Autowired
    private MdBomService mdBomService;

    @GetMapping
    public Result<PageUtil.PageResult<MdBom>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (keyword != null) params.put("keyword", keyword);
        if (status != null) params.put("status", status);
        return Result.success(PageUtil.toPage(mdBomService.list(params), page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MdBom> getById(@PathVariable Long id) {
        return Result.success(mdBomService.getById(id));
    }

    @PostMapping
    public Result<MdBom> create(@RequestBody MdBom bom) {
        return Result.success(mdBomService.create(bom));
    }

    @PutMapping
    public Result<MdBom> update(@RequestBody MdBom bom) {
        return Result.success(mdBomService.update(bom));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mdBomService.delete(id);
        return Result.success();
    }
}
