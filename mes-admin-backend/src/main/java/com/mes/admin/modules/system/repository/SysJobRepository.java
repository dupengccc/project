package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SysJobRepository extends JpaRepository<SysJob, Long>, JpaSpecificationExecutor<SysJob> {

    /** 按任务名称查询 */
    SysJob findByJobName(String jobName);

    /** 检查任务名称是否存在 */
    boolean existsByJobName(String jobName);

    /** 按状态查询 */
    List<SysJob> findByStatus(Integer status);
}
