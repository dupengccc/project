package com.mes.admin.modules.mes.cal.repository;

import com.mes.admin.modules.mes.cal.entity.CalPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CalPlanRepository extends JpaRepository<CalPlan, Long>, JpaSpecificationExecutor<CalPlan> {
}
