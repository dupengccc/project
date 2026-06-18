package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysRoleMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SysRoleMaterialRepository extends JpaRepository<SysRoleMaterial, Long> {

    /** 按角色ID查询关联的物料 */
    List<SysRoleMaterial> findByRoleId(Long roleId);

    /** 删除指定角色的物料关联 */
    void deleteByRoleId(Long roleId);
}
