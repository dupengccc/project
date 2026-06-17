package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {

    Optional<SysUser> findByUsername(String username);

    List<SysUser> findByDeptId(Long deptId);

    List<SysUser> findByStatus(Integer status);
}
