package com.mes.admin.modules.mes.wm.repository;

import com.mes.admin.modules.mes.wm.entity.WmArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WmAreaRepository extends JpaRepository<WmArea, Long>, JpaSpecificationExecutor<WmArea> {
}
