package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysRoleDept;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SysRoleDeptRepository extends JpaRepository<SysRoleDept, Long> {

    /** 按角色ID查询关联的组织ID */
    List<SysRoleDept> findByRoleId(Long roleId);

    /** 删除指定角色的关联数据 */
    void deleteByRoleId(Long roleId);
}
