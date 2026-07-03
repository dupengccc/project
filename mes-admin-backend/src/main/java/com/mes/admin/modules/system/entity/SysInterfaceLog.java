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
    @Column(name = "config_id")
    private Long configId;

    /** 接口编码 */
    @Column(name = "interface_code", length = 64)
    private String interfaceCode;

    /** 接口名称 */
    @Column(name = "interface_name", length = 128)
    private String interfaceName;

    /** 请求地址 */
    @Column(name = "request_url", length = 500)
    private String requestUrl;

    /** 请求方式 */
    @Column(name = "request_method", length = 16)
    private String requestMethod;

    /** 请求头（JSON格式） */
    @Column(name = "request_headers", columnDefinition = "TEXT")
    private String requestHeaders;

    /** 请求参数 */
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    /** 请求体 */
    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    /** 响应状态码 */
    @Column(name = "response_status")
    private Integer responseStatus;

    /** 响应头 */
    @Column(name = "response_headers", columnDefinition = "TEXT")
    private String responseHeaders;

    /** 响应体 */
    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    /** 响应时间（毫秒） */
    @Column(name = "response_time")
    private Long responseTime;

    /** 调用状态（0=成功 1=失败 2=超时 3=重试中） */
    @Column(name = "call_status")
    private Integer callStatus;

    /** 错误信息 */
    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    /** 异常类型 */
    @Column(name = "exception_type", length = 128)
    private String exceptionType;

    /** 堆栈信息 */
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    /** 业务状态（根据接口自定义，如0=处理中 1=成功 2=失败） */
    @Column(name = "biz_status", length = 16)
    private String bizStatus;

    /** 业务消息 */
    @Column(name = "biz_message", length = 255)
    private String bizMessage;

    /** 关联业务单号 */
    @Column(name = "biz_no", length = 64)
    private String bizNo;

    /** 调用来源系统 */
    @Column(name = "source_system", length = 64)
    private String sourceSystem;

    /** 调用来源模块 */
    @Column(name = "source_module", length = 64)
    private String sourceModule;

    /** 调用人ID */
    @Column(name = "operator_id")
    private Long operatorId;

    /** 调用人名称 */
    @Column(name = "operator_name", length = 64)
    private String operatorName;

    /** IP地址 */
    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    /** 调用时间 */
    @Column(name = "call_time")
    private Date callTime;

    /** 完成时间 */
    @Column(name = "finish_time")
    private Date finishTime;

    /** 重试次数 */
    @Column(name = "retry_count")
    private Integer retryCount;

    /** 是否已处理（0=未处理 1=已处理） */
    @Column(name = "processed")
    private Integer processed;

    /** 处理人 */
    @Column(name = "processor", length = 64)
    private String processor;

    /** 处理时间 */
    @Column(name = "process_time")
    private Date processTime;

    /** 处理备注 */
    @Column(name = "process_remark", length = 500)
    private String processRemark;

    /** 调用类型（0=主动调用 1=回调通知 2=定时任务 3=手动重试） */
    @Column(name = "call_type")
    private Integer callType;
}
