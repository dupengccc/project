package com.mes.admin.modules.mes.wm.repository;

import com.mes.admin.modules.mes.wm.entity.WmOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WmOutRepository extends JpaRepository<WmOut, Long>, JpaSpecificationExecutor<WmOut> {
}
