package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.TreeUtil;
import com.mes.admin.modules.system.entity.SysMenu;
import com.mes.admin.modules.system.service.SysMenuService;
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

import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/tree")
    public Result<List<TreeUtil.TreeNode<SysMenu>>> tree(@RequestParam(required = false) Long userId) {
        return Result.success(sysMenuService.getMenuTree(userId));
    }

    @GetMapping("/list")
    public Result<List<SysMenu>> list() {
        return Result.success(sysMenuService.findAll());
    }

    @GetMapping("/{id}")
    public Result<SysMenu> getById(@PathVariable Long id) {
        return Result.success(sysMenuService.findById(id));
    }

    @PostMapping
    public Result<SysMenu> create(@RequestBody SysMenu menu) {
        return Result.success(sysMenuService.create(menu));
    }

    @PutMapping
    public Result<SysMenu> update(@RequestBody SysMenu menu) {
        return Result.success(sysMenuService.update(menu));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysMenuService.delete(id);
        return Result.success();
    }
}
