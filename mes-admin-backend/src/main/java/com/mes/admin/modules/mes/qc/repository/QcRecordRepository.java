package com.mes.admin.modules.mes.qc.repository;

import com.mes.admin.modules.mes.qc.entity.QcRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QcRecordRepository extends JpaRepository<QcRecord, Long>, JpaSpecificationExecutor<QcRecord> {
}
