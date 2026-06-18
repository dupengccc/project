package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

    Optional<SysRole> findByRoleKey(String roleKey);

    List<SysRole> findByStatus(Integer status);
}
