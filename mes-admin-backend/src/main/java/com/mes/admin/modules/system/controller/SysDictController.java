package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.system.entity.SysDict;
import com.mes.admin.modules.system.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据字典管理（父子层级）
 */
@RestController
@RequestMapping("/system/dict")
public class SysDictController {

    @Autowired
    private SysDictService sysDictService;

    /**
     * 获取字典树（左侧树）
     */
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> tree() {
        return Result.success(sysDictService.getTree());
    }

    /**
     * 列表查询（支持按名称、编码、状态过滤）
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String dictName,
            @RequestParam(required = false) String dictCode,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("dictName", dictName);
        params.put("dictCode", dictCode);
        params.put("parentId", parentId);
        params.put("status", status);
        List<SysDict> all = sysDictService.findAll(params);
        int total = all.size();
        int from = Math.max(0, (page - 1) * pageSize);
        int to = Math.min(total, from + pageSize);
        List<SysDict> pageList = from >= total ? new java.util.ArrayList<>() : all.subList(from, to);
        Map<String, Object> data = new HashMap<>();
        data.put("list", pageList);
        data.put("total", total);
        return Result.success(data);
    }

    /**
     * 根据父编码查询子节点（供下拉框等场景使用）
     * 返回 [{ id, dictCode, dictName, dictValue, status }]
     */
    @GetMapping("/children")
    public Result<List<Map<String, Object>>> children(
            @RequestParam(required = false) String parentCode) {
        return Result.success(sysDictService.findByParentCode(parentCode));
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<SysDict> getById(@PathVariable Long id) {
        return Result.success(sysDictService.findById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<SysDict> create(@RequestBody SysDict dict) {
        return Result.success(sysDictService.create(dict));
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<SysDict> update(@RequestBody SysDict dict) {
        return Result.success(sysDictService.update(dict));
    }

    /**
     * 删除（含子节点）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysDictService.delete(id);
        return Result.success();
    }
}
