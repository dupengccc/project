package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 定时任务调度表
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_job")
public class SysJob extends BaseEntity {

    /** 任务名称 */
    @Column(length = 64)
    private String jobName;

    /** 任务组名 */
    @Column(length = 64)
    private String jobGroup;

    /** 调用目标字符串（格式：beanName.method(params) 或 beanName.method） */
    @Column(length = 500)
    private String invokeTarget;

    /** cron表达式 */
    @Column(length = 255)
    private String cronExpression;

    /** 错误执行策略（1立即执行 2执行一次 3放弃执行） */
    private Integer misfirePolicy;

    /** 是否并发执行（0允许 1禁止） */
    private Integer concurrent;

    /** 状态（0正常 1暂停） */
    private Integer status;

    /** 上次执行时间 */
    @Column(name = "prev_time")
    private Date prevTime;

    /** 下次执行时间 */
    @Column(name = "next_time")
    private Date nextTime;
}
