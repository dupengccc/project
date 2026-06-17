package com.mes.admin.modules.mes.qc.repository;

import com.mes.admin.modules.mes.qc.entity.QcTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QcTemplateRepository extends JpaRepository<QcTemplate, Long>, JpaSpecificationExecutor<QcTemplate> {
}
