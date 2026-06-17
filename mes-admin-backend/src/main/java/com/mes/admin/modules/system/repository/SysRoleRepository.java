package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SysRoleRepository extends JpaRepository<SysRole, Long>, JpaSpecificationExecutor<SysRole> {

    Optional<SysRole> findByRoleKey(String roleKey);
}
