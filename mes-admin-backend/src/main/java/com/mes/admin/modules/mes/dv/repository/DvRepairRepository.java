package com.mes.admin.modules.mes.dv.repository;

import com.mes.admin.modules.mes.dv.entity.DvRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DvRepairRepository extends JpaRepository<DvRepair, Long>, JpaSpecificationExecutor<DvRepair> {
}
