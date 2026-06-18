package com.mes.admin.modules.system.util;

import com.mes.admin.modules.system.entity.SysOrg;
import com.mes.admin.modules.system.entity.SysRole;
import com.mes.admin.modules.system.entity.SysUser;
import com.mes.admin.modules.system.repository.SysOrgRepository;
import com.mes.admin.modules.system.repository.SysRoleDeptRepository;
import com.mes.admin.modules.system.repository.SysRoleRepository;
import com.mes.admin.modules.system.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据范围（数据权限）工具类
 *
 * 核心方法：getVisibleOrgIds(SysUser) — 返回当前用户可见的组织 ID 列表
 *
 * 数据范围对照表：
 *   1 = 全部数据       → 返回 null（表示不过滤）
 *   2 = 自定义数据     → 取 sys_role_dept 关联表中配置的组织 ID 列表
 *   3 = 本部门数据     → 返回当前用户所在组织（deptId）
 *   4 = 本部门及以下   → 当前组织 + 递归展开的所有下级组织
 *   5 = 仅本人数据     → 返回空列表（调用方需按 user 过滤）
 *
 * 约定：若返回 null，代表"全部数据"，不要加组织过滤条件。
 * 若返回空集合，代表"无任何组织可见"。
 */
@Component
public class DataScopeHelper {

    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private SysRoleDeptRepository sysRoleDeptRepository;

    @Autowired
    private SysOrgRepository sysOrgRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    /**
     * 获取当前登录用户可见的组织 ID 列表
     * 优先按用户的角色编码推断；若缺失角色，返回"全部数据"
     *
     * @param user 当前登录用户，可为 null（默认 admin 行为）
     * @return 可见的组织 ID 集合，返回 null 表示不做过滤（全部数据）
     */
    public Set<Long> getVisibleOrgIds(SysUser user) {
        if (user == null) {
            // 缺失上下文 → 视为超级管理员（便于演示）
            return null;
        }
        SysRole role = findRoleByUser(user);
        if (role == null) {
            // 无角色，默认全部数据（演示友好默认）
            return null;
        }
        String scope = role.getDataScope();
        if (scope == null || scope.isEmpty()) {
            return null;
        }

        switch (scope) {
            case "1": // 全部数据
                return null;

            case "2": // 自定义数据
                List<Long> customIds = findDeptIdsByRoleId(role.getId());
                return new HashSet<>(customIds);

            case "3": // 本部门
                if (user.getDeptId() != null) {
                    return Collections.singleton(user.getDeptId());
                }
                return Collections.emptySet();

            case "4": { // 本部门及以下
                Set<Long> result = new HashSet<>();
                if (user.getDeptId() != null) {
                    collectSelfAndChildren(user.getDeptId(), result);
                }
                return result;
            }
            case "5": // 仅本人
                return Collections.emptySet();

            default:
                return null;
        }
    }

    /**
     * 判断当前用户是否"仅本人"数据范围（业务层用于判断是否要按 createBy 过滤）
     */
    public boolean isSelfOnly(SysUser user) {
        if (user == null) return false;
        SysRole role = findRoleByUser(user);
        return role != null && "5".equals(role.getDataScope());
    }

    /**
     * 判断当前用户是否"全部数据"（通常是超级管理员）
     */
    public boolean isAllData(SysUser user) {
        if (user == null) return true;
        SysRole role = findRoleByUser(user);
        if (role == null) return true;
        return "1".equals(role.getDataScope());
    }

    // ============== 私有辅助方法 ==============

    /**
     * 根据用户对象推导出对应角色。
     * 本项目目前尚未引入"用户-角色"表，按以下规则推断角色：
     *   1) 优先按 username 匹配 roleKey（如 admin → super_admin）
     *   2) 若数据库可用，查 SysRole 表
     *   3) 数据库不可用时：admin 视为"全部数据"角色，其他视为"本部门及以下"
     */
    private SysRole findRoleByUser(SysUser user) {
        if (user == null) return null;

        // 1. 查数据库（优先）
        try {
            List<SysRole> all = sysRoleRepository.findAll();
            if (all != null && !all.isEmpty()) {
                // admin 用户 → roleKey 包含 'super' 或 'admin' 的角色
                for (SysRole r : all) {
                    if (r.getRoleKey() != null
                            && (r.getRoleKey().equalsIgnoreCase("super_admin")
                                || r.getRoleKey().equalsIgnoreCase("admin"))) {
                        if ("admin".equals(user.getUsername())) return r;
                    }
                }
                // 其他用户：随便取一个非超级管理员的角色
                for (SysRole r : all) {
                    if (!"1".equals(r.getDataScope())) return r;
                }
                return all.get(0);
            }
        } catch (Exception ignored) {
        }

        // 2. 数据库不可用 → 构建内存模拟角色
        SysRole mock = new SysRole();
        if ("admin".equals(user.getUsername())) {
            mock.setId(1L);
            mock.setRoleName("超级管理员");
            mock.setRoleKey("super_admin");
            mock.setDataScope("1"); // 全部数据
        } else {
            mock.setId(2L);
            mock.setRoleName("普通用户");
            mock.setRoleKey("user");
            mock.setDataScope("4"); // 本部门及以下
        }
        return mock;
    }

    private List<Long> findDeptIdsByRoleId(Long roleId) {
        if (roleId == null) return Collections.emptyList();
        try {
            return sysRoleDeptRepository.findByRoleId(roleId).stream()
                    .map(rd -> rd.getDeptId())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception ignored) {
            // Mock 模式：给超级管理员返回组织树根节点，演示用
            if (roleId == 1L) {
                return Collections.singletonList(1L);
            }
            return Collections.emptyList();
        }
    }

    /**
     * 把给定组织及其所有下级组织 ID 收集到结果集中（树形递归）
     */
    private void collectSelfAndChildren(Long parentId, Set<Long> result) {
        if (parentId == null) return;
        result.add(parentId);
        try {
            List<SysOrg> children = sysOrgRepository.findByParentId(parentId);
            if (children != null) {
                for (SysOrg child : children) {
                    collectSelfAndChildren(child.getId(), result);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
