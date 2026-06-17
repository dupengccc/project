package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.system.entity.SysRole;
import com.mes.admin.modules.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.success(sysRoleService.findAll());
    }

    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.findById(id));
    }

    @PostMapping
    public Result<SysRole> create(@RequestBody SysRole role) {
        return Result.success(sysRoleService.create(role));
    }

    @PutMapping
    public Result<SysRole> update(@RequestBody SysRole role) {
        return Result.success(sysRoleService.update(role));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.delete(id);
        return Result.success();
    }
}
