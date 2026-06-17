package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SysOrgRepository extends JpaRepository<SysOrg, Long>, JpaSpecificationExecutor<SysOrg> {

    Optional<SysOrg> findByOrgCode(String orgCode);

    List<SysOrg> findByParentId(Long parentId);

    List<SysOrg> findByOrgType(String orgType);
}
