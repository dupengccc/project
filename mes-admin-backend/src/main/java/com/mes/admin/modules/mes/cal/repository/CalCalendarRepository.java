package com.mes.admin.modules.mes.cal.repository;

import com.mes.admin.modules.mes.cal.entity.CalCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CalCalendarRepository extends JpaRepository<CalCalendar, Long>, JpaSpecificationExecutor<CalCalendar> {
}
