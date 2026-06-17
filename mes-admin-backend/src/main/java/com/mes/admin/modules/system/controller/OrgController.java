package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.TreeUtil;
import com.mes.admin.modules.system.entity.SysOrg;
import com.mes.admin.modules.system.service.SysOrgService;
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
@RequestMapping("/system/org")
public class OrgController {

    @Autowired
    private SysOrgService sysOrgService;

    @GetMapping("/tree")
    public Result<List<TreeUtil.TreeNode<SysOrg>>> tree() {
        return Result.success(sysOrgService.getTree());
    }

    @GetMapping("/list")
    public Result<List<SysOrg>> list() {
        return Result.success(sysOrgService.findAll());
    }

    @GetMapping("/{id}")
    public Result<SysOrg> getById(@PathVariable Long id) {
        return Result.success(sysOrgService.findById(id));
    }

    @PostMapping
    public Result<SysOrg> create(@RequestBody SysOrg org) {
        return Result.success(sysOrgService.create(org));
    }

    @PutMapping
    public Result<SysOrg> update(@RequestBody SysOrg org) {
        return Result.success(sysOrgService.update(org));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysOrgService.delete(id);
        return Result.success();
    }
}
