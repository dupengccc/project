package com.mes.admin.modules.mes.tm.repository;

import com.mes.admin.modules.mes.tm.entity.TmTool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TmToolRepository extends JpaRepository<TmTool, Long>, JpaSpecificationExecutor<TmTool> {
}
