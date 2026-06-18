package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysLoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 登录日志 Repository
 */
public interface SysLoginLogRepository extends JpaRepository<SysLoginLog, Long>,
        JpaSpecificationExecutor<SysLoginLog> {

    /**
     * 按工号（用户账号）查询
     */
    List<SysLoginLog> findByEmpNo(String empNo);

    /**
     * 查询某用户最新一条登录记录（用于登录成功后写入）
     */
    Optional<SysLoginLog> findTopByEmpNoOrderByIdDesc(String empNo);

    /**
     * 按时间范围查询
     */
    List<SysLoginLog> findByLoginTimeBetween(Date start, Date end);
}
