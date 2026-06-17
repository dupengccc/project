package com.mes.admin.modules.mes.md.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.md.entity.MdMaterial;
import com.mes.admin.modules.mes.md.service.MdMaterialService;
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
@RequestMapping("/api/mes/md/materials")
public class MdMaterialController {

    @Autowired
    private MdMaterialService mdMaterialService;

    @GetMapping
    public Result<PageUtil.PageResult<MdMaterial>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String materialType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (keyword != null) params.put("keyword", keyword);
        if (materialType != null) params.put("materialType", materialType);
        if (status != null) params.put("status", status);
        return Result.success(PageUtil.toPage(mdMaterialService.list(params), page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MdMaterial> getById(@PathVariable Long id) {
        return Result.success(mdMaterialService.getById(id));
    }

    @PostMapping
    public Result<MdMaterial> create(@RequestBody MdMaterial material) {
        return Result.success(mdMaterialService.create(material));
    }

    @PutMapping
    public Result<MdMaterial> update(@RequestBody MdMaterial material) {
        return Result.success(mdMaterialService.update(material));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mdMaterialService.delete(id);
        return Result.success();
    }
}
