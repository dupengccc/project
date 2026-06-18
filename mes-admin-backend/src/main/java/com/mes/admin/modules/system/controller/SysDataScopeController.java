package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.system.entity.SysRole;
import com.mes.admin.modules.system.service.SysDataScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据权限管理
 */
@RestController
@RequestMapping("/system/data-scope")
public class SysDataScopeController {

    @Autowired
    private SysDataScopeService dataScopeService;

    @GetMapping("/role-list")
    public Result<Map<String, Object>> roleList(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> params = new HashMap<>();
        if (roleName != null && !roleName.isEmpty()) params.put("roleName", roleName);
        if (status != null) params.put("status", status);
        List<SysRole> list = dataScopeService.findRoles(params);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", list.size());
        return Result.success(data);
    }

    /**
     * 获取单个角色的数据范围配置（包含组织和物料）
     */
    @GetMapping("/role/{roleId}")
    public Result<Map<String, Object>> getRoleDataScope(@PathVariable Long roleId) {
        SysRole role = dataScopeService.findRoleById(roleId);
        List<Long> deptIds = dataScopeService.findDeptIdsByRoleId(roleId);
        List<Long> materialIds = dataScopeService.findMaterialIdsByRoleId(roleId);
        Map<String, Object> data = new HashMap<>();
        data.put("role", role);
        data.put("deptIds", deptIds);
        data.put("materialIds", materialIds);
        return Result.success(data);
    }

    /**
     * 分配组织数据权限
     */
    @PostMapping("/assign")
    public Result<Void> assign(@RequestBody Map<String, Object> body) {
        Number roleIdNum = body.get("roleId") != null ? ((Number) body.get("roleId")) : null;
        Long roleId = roleIdNum != null ? roleIdNum.longValue() : null;
        String dataScope = body.get("dataScope") != null ? body.get("dataScope").toString() : "1";
        @SuppressWarnings("unchecked")
        List<Long> deptIds = (List<Long>) body.get("deptIds");
        dataScopeService.assignDataScope(roleId, dataScope, deptIds);
        return Result.success();
    }

    /**
     * 分配物料数据权限
     */
    @PostMapping("/assign-material")
    public Result<Void> assignMaterial(@RequestBody Map<String, Object> body) {
        Number roleIdNum = body.get("roleId") != null ? ((Number) body.get("roleId")) : null;
        Long roleId = roleIdNum != null ? roleIdNum.longValue() : null;
        @SuppressWarnings("unchecked")
        List<Long> materialIds = (List<Long>) body.get("materialIds");
        dataScopeService.assignMaterialScope(roleId, materialIds);
        return Result.success();
    }

    /**
     * 保存/更新角色（仅角色基本信息，不涉及组织）
     */
    @PostMapping("/role")
    public Result<SysRole> saveRole(@RequestBody SysRole role) {
        return Result.success(dataScopeService.saveRole(role));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/role/{roleId}")
    public Result<Void> deleteRole(@PathVariable Long roleId) {
        dataScopeService.deleteRole(roleId);
        return Result.success();
    }

    /**
     * 获取组织树
     */
    @GetMapping("/org-tree")
    public Result<List<Map<String, Object>>> orgTree() {
        return Result.success(dataScopeService.findOrgTree());
    }

    /**
     * 获取物料列表（支持按类型过滤）
     */
    @GetMapping("/materials")
    public Result<List<Map<String, Object>>> materials(
            @RequestParam(required = false) String materialType) {
        return Result.success(dataScopeService.findMaterials(materialType));
    }
}
