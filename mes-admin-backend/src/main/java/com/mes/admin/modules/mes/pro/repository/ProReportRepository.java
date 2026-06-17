package com.mes.admin.modules.mes.pro.repository;

import com.mes.admin.modules.mes.pro.entity.ProReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProReportRepository extends JpaRepository<ProReport, Long>, JpaSpecificationExecutor<ProReport> {
}
