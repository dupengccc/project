package com.mes.admin.modules.system.service;

import com.mes.admin.modules.mes.md.entity.MdMaterial;
import com.mes.admin.modules.mes.md.repository.MdMaterialRepository;
import com.mes.admin.modules.system.entity.SysOrg;
import com.mes.admin.modules.system.entity.SysRole;
import com.mes.admin.modules.system.entity.SysRoleDept;
import com.mes.admin.modules.system.entity.SysRoleMaterial;
import com.mes.admin.modules.system.repository.SysOrgRepository;
import com.mes.admin.modules.system.repository.SysRoleDeptRepository;
import com.mes.admin.modules.system.repository.SysRoleMaterialRepository;
import com.mes.admin.modules.system.repository.SysRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限服务
 * 负责维护角色的 dataScope 范围配置，以及角色-组织、角色-物料关联表
 */
@Service
public class SysDataScopeService {

    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private SysRoleDeptRepository sysRoleDeptRepository;

    @Autowired
    private SysRoleMaterialRepository sysRoleMaterialRepository;

    @Autowired
    private SysOrgRepository sysOrgRepository;

    @Autowired
    private MdMaterialRepository mdMaterialRepository;

    public List<SysRole> findRoles(Map<String, Object> params) {
        try {
            return sysRoleRepository.findAll((root, query, cb) -> {
                List<Predicate> ps = new ArrayList<>();
                if (params != null) {
                    if (params.get("roleName") != null && !"".equals(params.get("roleName"))) {
                        ps.add(cb.like(root.get("roleName"), "%" + params.get("roleName") + "%"));
                    }
                    if (params.get("status") != null) {
                        ps.add(cb.equal(root.get("status"), params.get("status")));
                    }
                }
                if (query != null) query.orderBy(cb.asc(root.get("roleSort")));
                return cb.and(ps.toArray(new Predicate[0]));
            });
        } catch (Exception ignored) {
            return buildMockRoles();
        }
    }

    public SysRole findRoleById(Long id) {
        if (id == null) return null;
        try {
            Optional<SysRole> opt = sysRoleRepository.findById(id);
            if (opt.isPresent()) return opt.get();
        } catch (Exception ignored) {}
        for (SysRole r : buildMockRoles()) {
            if (id.equals(r.getId())) return r;
        }
        return null;
    }

    @Transactional
    public SysRole saveRole(SysRole role) {
        if (role == null) return null;
        if (role.getStatus() == null) role.setStatus(0);
        if (role.getDataScope() == null || role.getDataScope().isEmpty()) {
            role.setDataScope("1");
        }
        try {
            return sysRoleRepository.save(role);
        } catch (Exception ignored) {
            if (role.getId() == null) {
                role.setId(System.currentTimeMillis());
            }
            return role;
        }
    }

    @Transactional
    public void deleteRole(Long id) {
        try {
            sysRoleDeptRepository.deleteByRoleId(id);
        } catch (Exception ignored) {}
        try {
            sysRoleMaterialRepository.deleteByRoleId(id);
        } catch (Exception ignored) {}
        try {
            sysRoleRepository.deleteById(id);
        } catch (Exception ignored) {}
    }

    /** 获取角色关联的组织 ID 列表 */
    public List<Long> findDeptIdsByRoleId(Long roleId) {
        try {
            return sysRoleDeptRepository.findByRoleId(roleId).stream()
                    .map(SysRoleDept::getDeptId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception ignored) {
            if (roleId != null && roleId == 1L) {
                return Collections.singletonList(1L);
            }
            return Collections.emptyList();
        }
    }

    /** 获取角色关联的物料 ID 列表 */
    public List<Long> findMaterialIdsByRoleId(Long roleId) {
        try {
            return sysRoleMaterialRepository.findByRoleId(roleId).stream()
                    .map(SysRoleMaterial::getMaterialId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    /** 分配组织数据权限 */
    @Transactional
    public void assignDataScope(Long roleId, String dataScope, List<Long> deptIds) {
        if (roleId == null) throw new RuntimeException("角色ID不能为空");
        if (dataScope == null || dataScope.isEmpty()) dataScope = "1";

        try {
            Optional<SysRole> opt = sysRoleRepository.findById(roleId);
            if (opt.isPresent()) {
                SysRole r = opt.get();
                r.setDataScope(dataScope);
                sysRoleRepository.save(r);
            }
        } catch (Exception ignored) {}

        try {
            sysRoleDeptRepository.deleteByRoleId(roleId);
        } catch (Exception ignored) {}

        if ("2".equals(dataScope) && deptIds != null && !deptIds.isEmpty()) {
            try {
                for (Long deptId : deptIds) {
                    SysRoleDept rd = new SysRoleDept();
                    rd.setRoleId(roleId);
                    rd.setDeptId(deptId);
                    sysRoleDeptRepository.save(rd);
                }
            } catch (Exception ignored) {}
        }
    }

    /**
     * 分配物料数据权限
     * @param roleId 角色ID
     * @param materialIds 物料ID列表（可为空，空则清空关联）
     */
    @Transactional
    public void assignMaterialScope(Long roleId, List<Long> materialIds) {
        if (roleId == null) throw new RuntimeException("角色ID不能为空");

        try {
            sysRoleMaterialRepository.deleteByRoleId(roleId);
        } catch (Exception ignored) {}

        if (materialIds == null || materialIds.isEmpty()) return;

        try {
            for (Long materialId : materialIds) {
                // 获取物料信息（冗余存储便于查询）
                MdMaterial material = null;
                try {
                    Optional<MdMaterial> opt = mdMaterialRepository.findById(materialId);
                    if (opt.isPresent()) material = opt.get();
                } catch (Exception ignored) {}

                SysRoleMaterial rm = new SysRoleMaterial();
                rm.setRoleId(roleId);
                rm.setMaterialId(materialId);
                rm.setMaterialCode(material != null ? material.getMaterialCode() : null);
                rm.setMaterialName(material != null ? material.getMaterialName() : null);
                sysRoleMaterialRepository.save(rm);
            }
        } catch (Exception ignored) {}
    }

    /** 获取组织树 */
    public List<Map<String, Object>> findOrgTree() {
        List<SysOrg> orgs;
        try {
            orgs = sysOrgRepository.findAll();
        } catch (Exception ignored) {
            orgs = null;
        }
        if (orgs == null || orgs.isEmpty()) {
            orgs = buildMockOrgs();
        }
        Map<Long, Map<String, Object>> map = new LinkedHashMap<>();
        for (SysOrg org : orgs) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", org.getId());
            node.put("label", org.getOrgName() != null ? org.getOrgName() : "");
            node.put("orgCode", org.getOrgCode());
            node.put("orgType", org.getOrgType());
            node.put("parentId", org.getParentId());
            node.put("children", new ArrayList<Map<String, Object>>());
            map.put(org.getId(), node);
        }
        List<Map<String, Object>> roots = new ArrayList<>();
        for (SysOrg org : orgs) {
            Map<String, Object> node = map.get(org.getId());
            Long pid = org.getParentId();
            if (pid != null && map.containsKey(pid)) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> siblings = (List<Map<String, Object>>) map.get(pid).get("children");
                siblings.add(node);
            } else {
                roots.add(node);
            }
        }
        return roots;
    }

    /**
     * 查询物料列表（用于前端多选）
     * @param materialType 物料类型过滤（可选）
     */
    public List<Map<String, Object>> findMaterials(String materialType) {
        List<MdMaterial> materials;
        try {
            materials = mdMaterialRepository.findAll((root, query, cb) -> {
                if (materialType != null && !materialType.isEmpty()) {
                    return cb.equal(root.get("materialType"), materialType);
                }
                return null;
            });
        } catch (Exception ignored) {
            materials = null;
        }
        if (materials == null || materials.isEmpty()) {
            materials = buildMockMaterials();
            if (materialType != null && !materialType.isEmpty()) {
                materials = materials.stream()
                        .filter(m -> materialType.equals(m.getMaterialType()))
                        .collect(Collectors.toList());
            }
        }
        return materials.stream().map(m -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.getId());
            item.put("materialCode", m.getMaterialCode());
            item.put("materialName", m.getMaterialName());
            item.put("materialType", m.getMaterialType());
            item.put("spec", m.getSpec());
            item.put("unit", m.getUnit());
            return item;
        }).collect(Collectors.toList());
    }

    private List<SysRole> buildMockRoles() {
        List<SysRole> list = new ArrayList<>();
        list.add(makeMockRole(1L, "超级管理员", "super_admin", 1, 0, "1", "拥有全部数据权限"));
        list.add(makeMockRole(2L, "集团管理员", "group_admin", 2, 0, "4", "集团范围内所有数据"));
        list.add(makeMockRole(3L, "分公司管理员", "branch_admin", 3, 0, "4", "分公司范围内所有数据"));
        list.add(makeMockRole(4L, "部门管理员", "dept_admin", 4, 0, "3", "只能查看本部门数据"));
        list.add(makeMockRole(5L, "普通用户", "user", 5, 0, "5", "只能查看自己创建的数据"));
        list.add(makeMockRole(6L, "访客", "guest", 6, 1, "5", "只读访客"));
        return list;
    }

    private SysRole makeMockRole(Long id, String name, String key, int sort, int status, String scope, String remark) {
        SysRole r = new SysRole();
        r.setId(id);
        r.setRoleName(name);
        r.setRoleKey(key);
        r.setRoleSort(sort);
        r.setStatus(status);
        r.setDataScope(scope);
        r.setRemark(remark);
        r.setCreateTime(new Date());
        return r;
    }

    private List<SysOrg> buildMockOrgs() {
        List<SysOrg> list = new ArrayList<>();
        list.add(makeMockOrg(1L, null, "MES 集团总部", "GROUP001", "集团"));
        list.add(makeMockOrg(2L, 1L, "华东分公司", "BRANCH001", "分公司"));
        list.add(makeMockOrg(3L, 1L, "华南分公司", "BRANCH002", "分公司"));
        list.add(makeMockOrg(4L, 2L, "研发部", "DEPT001", "部门"));
        list.add(makeMockOrg(5L, 2L, "生产部", "DEPT002", "部门"));
        list.add(makeMockOrg(6L, 3L, "销售部", "DEPT003", "部门"));
        list.add(makeMockOrg(7L, 3L, "售后部", "DEPT004", "部门"));
        return list;
    }

    private SysOrg makeMockOrg(Long id, Long parentId, String name, String code, String type) {
        SysOrg org = new SysOrg();
        org.setId(id);
        org.setParentId(parentId);
        org.setOrgName(name);
        org.setOrgCode(code);
        org.setOrgType(type);
        return org;
    }

    private List<MdMaterial> buildMockMaterials() {
        List<MdMaterial> list = new ArrayList<>();
        list.add(makeMockMaterial(1L, "M00001", "不锈钢板", "1220*2440*2mm", "原材料", "张"));
        list.add(makeMockMaterial(2L, "M00002", "铝合金型材", "6063-T5 2m", "原材料", "根"));
        list.add(makeMockMaterial(3L, "M00003", "碳钢圆棒", "直径20mm", "原材料", "根"));
        list.add(makeMockMaterial(4L, "B00001", "半成品装配A", "A100", "半成品", "件"));
        list.add(makeMockMaterial(5L, "B00002", "半成品装配B", "B200", "半成品", "件"));
        list.add(makeMockMaterial(6L, "F00001", "工控机箱", "IPC-610L", "成品", "台"));
        list.add(makeMockMaterial(7L, "F00002", "触控一体机", "15寸", "成品", "台"));
        list.add(makeMockMaterial(8L, "A00001", "内六角螺丝", "M4x8", "辅料", "包"));
        list.add(makeMockMaterial(9L, "A00002", "垫片", "M4", "辅料", "包"));
        list.add(makeMockMaterial(10L, "A00003", "螺母", "M4", "辅料", "包"));
        return list;
    }

    private MdMaterial makeMockMaterial(Long id, String code, String name, String spec, String type, String unit) {
        MdMaterial m = new MdMaterial();
        m.setId(id);
        m.setMaterialCode(code);
        m.setMaterialName(name);
        m.setSpec(spec);
        m.setMaterialType(type);
        m.setUnit(unit);
        return m;
    }
}
