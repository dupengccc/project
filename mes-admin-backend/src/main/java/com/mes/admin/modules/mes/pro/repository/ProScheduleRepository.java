package com.mes.admin.modules.mes.pro.repository;

import com.mes.admin.modules.mes.pro.entity.ProSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProScheduleRepository extends JpaRepository<ProSchedule, Long>, JpaSpecificationExecutor<ProSchedule> {
}
