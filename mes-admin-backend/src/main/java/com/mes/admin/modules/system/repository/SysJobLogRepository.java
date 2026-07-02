package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysJobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysJobLogRepository extends JpaRepository<SysJobLog, Long>, JpaSpecificationExecutor<SysJobLog> {
}
