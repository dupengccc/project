package com.mes.admin.modules.mes.wm.repository;

import com.mes.admin.modules.mes.wm.entity.WmLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WmLocationRepository extends JpaRepository<WmLocation, Long>, JpaSpecificationExecutor<WmLocation> {
}
