package com.mes.admin.modules.mes.cal.repository;

import com.mes.admin.modules.mes.cal.entity.CalShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CalShiftRepository extends JpaRepository<CalShift, Long>, JpaSpecificationExecutor<CalShift> {
}
