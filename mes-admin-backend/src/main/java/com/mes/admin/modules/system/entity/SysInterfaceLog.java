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
 * 接口调用日志实体
 * 记录每次接口调用的详细信息，用于监控和排查
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_interface_log")
public class SysInterfaceLog extends BaseEntity {

    /** 关联的接口配置ID */
    private Long configId;

    /** 接口编码 */
    @Column(length = 64)
    private String interfaceCode;

    /** 接口名称 */
    @Column(length = 128)
    private String interfaceName;

    /** 请求地址 */
    @Column(length = 500)
    private String requestUrl;

    /** 请求方式 */
    @Column(length = 16)
    private String requestMethod;

    /** 请求头（JSON格式） */
    @Column(columnDefinition = "TEXT")
    private String requestHeaders;

    /** 请求参数 */
    @Column(columnDefinition = "TEXT")
    private String requestParams;

    /** 请求体 */
    @Column(columnDefinition = "TEXT")
    private String requestBody;

    /** 响应状态码 */
    private Integer responseStatus;

    /** 响应头 */
    @Column(columnDefinition = "TEXT")
    private String responseHeaders;

    /** 响应体 */
    @Column(columnDefinition = "TEXT")
    private String responseBody;

    /** 响应时间（毫秒） */
    private Long responseTime;

    /** 调用状态（0=成功 1=失败 2=超时 3=重试中） */
    private Integer callStatus;

    /** 错误信息 */
    @Column(columnDefinition = "TEXT")
    private String errorMsg;

    /** 异常类型 */
    @Column(length = 128)
    private String exceptionType;

    /** 堆栈信息 */
    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    /** 业务状态（根据接口自定义，如0=处理中 1=成功 2=失败） */
    @Column(length = 16)
    private String bizStatus;

    /** 业务消息 */
    @Column(length = 255)
    private String bizMessage;

    /** 关联业务单号 */
    @Column(length = 64)
    private String bizNo;

    /** 调用来源系统 */
    @Column(length = 64)
    private String sourceSystem;

    /** 调用来源模块 */
    @Column(length = 64)
    private String sourceModule;

    /** 调用人ID */
    private Long operatorId;

    /** 调用人名称 */
    @Column(length = 64)
    private String operatorName;

    /** IP地址 */
    @Column(length = 64)
    private String ipAddress;

    /** 调用时间 */
    private Date callTime;

    /** 完成时间 */
    private Date finishTime;

    /** 重试次数 */
    private Integer retryCount;

    /** 是否已处理（0=未处理 1=已处理） */
    private Integer processed;

    /** 处理人 */
    @Column(length = 64)
    private String processor;

    /** 处理时间 */
    private Date processTime;

    /** 处理备注 */
    @Column(length = 500)
    private String processRemark;

    /** 调用类型（0=主动调用 1=回调通知 2=定时任务 3=手动重试） */
    private Integer callType;
}
