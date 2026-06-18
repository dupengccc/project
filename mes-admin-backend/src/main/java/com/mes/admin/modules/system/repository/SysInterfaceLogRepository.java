package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysInterfaceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SysInterfaceLogRepository extends JpaRepository<SysInterfaceLog, Long>, JpaSpecificationExecutor<SysInterfaceLog> {

    /** 根据接口编码查询 */
    List<SysInterfaceLog> findByInterfaceCode(String interfaceCode);

    /** 根据调用状态查询 */
    List<SysInterfaceLog> findByCallStatus(Integer callStatus);

    /** 根据业务单号查询 */
    List<SysInterfaceLog> findByBizNo(String bizNo);

    /** 查询最近的调用日志 */
    List<SysInterfaceLog> findTop100ByOrderByCallTimeDesc();

    /** 查询指定接口最近的调用日志 */
    List<SysInterfaceLog> findTop50ByInterfaceCodeOrderByCallTimeDesc(String interfaceCode);

    /** 查询异常日志 */
    List<SysInterfaceLog> findByCallStatusInOrderByCallTimeDesc(List<Integer> statuses);

    /** 更新处理状态 */
    @Modifying
    @Query("UPDATE SysInterfaceLog SET processed = 1, processor = :processor, processTime = :processTime, processRemark = :remark WHERE id = :id")
    void updateProcessStatus(@Param("id") Long id, @Param("processor") String processor, @Param("processTime") Date processTime, @Param("remark") String remark);

    /** 统计每日调用次数 */
    @Query("SELECT COUNT(*) FROM SysInterfaceLog WHERE callTime >= :startTime AND callTime < :endTime")
    Long countByCallTimeBetween(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /** 统计每日异常次数 */
    @Query("SELECT COUNT(*) FROM SysInterfaceLog WHERE callStatus = 1 AND callTime >= :startTime AND callTime < :endTime")
    Long countErrorByCallTimeBetween(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
