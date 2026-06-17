package com.mes.admin.modules.mes.qc.repository;

import com.mes.admin.modules.mes.qc.entity.QcDefect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QcDefectRepository extends JpaRepository<QcDefect, Long>, JpaSpecificationExecutor<QcDefect> {
}
