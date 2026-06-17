package com.mes.admin.modules.mes.dv.repository;

import com.mes.admin.modules.mes.dv.entity.DvCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DvCheckRepository extends JpaRepository<DvCheck, Long>, JpaSpecificationExecutor<DvCheck> {
}
