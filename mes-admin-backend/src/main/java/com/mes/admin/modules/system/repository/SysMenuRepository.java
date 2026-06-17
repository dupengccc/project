package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SysMenuRepository extends JpaRepository<SysMenu, Long>, JpaSpecificationExecutor<SysMenu> {

    List<SysMenu> findByParentId(Long parentId);
}
