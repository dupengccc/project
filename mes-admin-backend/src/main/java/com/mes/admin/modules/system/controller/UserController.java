package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.system.entity.SysUser;
import com.mes.admin.modules.system.service.SysUserService;
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
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/list")
    public Result<PageUtil.PageResult<SysUser>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long deptId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (username != null) params.put("username", username);
        if (status != null) params.put("status", status);
        if (deptId != null) params.put("deptId", deptId);
        List<SysUser> users = sysUserService.findAll(params);
        return Result.success(PageUtil.toPage(users, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(sysUserService.findById(id));
    }

    @PostMapping
    public Result<SysUser> create(@RequestBody SysUser user) {
        return Result.success(sysUserService.create(user));
    }

    @PutMapping
    public Result<SysUser> update(@RequestBody SysUser user) {
        return Result.success(sysUserService.update(user));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.success();
    }
}
