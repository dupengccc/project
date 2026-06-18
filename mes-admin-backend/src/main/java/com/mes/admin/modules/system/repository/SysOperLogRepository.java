package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysOperLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * 操作日志 Repository
 */
public interface SysOperLogRepository extends JpaRepository<SysOperLog, Long>,
        JpaSpecificationExecutor<SysOperLog> {

    /** 按工号查询 */
    List<SysOperLog> findByEmpNo(String empNo);

    /** 按时间范围查询 */
    List<SysOperLog> findByOperTimeBetween(Date start, Date end);
}
