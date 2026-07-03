package com.mes.admin.modules.system.entity;

import com.mes.admin.common.util.IdGenerator;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Date;

/**
 * 定时任务调度日志表
 * 日志量大、与任务配置生命周期不同步，故不继承 BaseEntity 审计字段
 */
@Data
@Entity
@Table(name = "sys_job_log")
public class SysJobLog {

    @Id
    @Column(name = "id", precision = 20)
    private Long id;

    /** 任务名称 */
    @Column(name = "job_name", length = 64)
    private String jobName;

    /** 任务组名 */
    @Column(name = "job_group", length = 64)
    private String jobGroup;

    /** 调用目标字符串 */
    @Column(name = "invoke_target", length = 500)
    private String invokeTarget;

    /** 日志信息 */
    @Column(name = "job_message", length = 500)
    private String jobMessage;

    /** 执行状态（0成功 1失败） */
    @Column(name = "status")
    private Integer status;

    /** 异常信息 */
    @Column(name = "exception_info", columnDefinition = "TEXT")
    private String exceptionInfo;

    /** 开始时间 */
    @Column(name = "start_time")
    private Date startTime;

    /** 结束时间 */
    @Column(name = "end_time")
    private Date endTime;

    /** 耗时（毫秒） */
    @Column(name = "cost_time")
    private Long costTime;

    /** 新增前自动生成ID */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = IdGenerator.generateId();
        }
    }
}
