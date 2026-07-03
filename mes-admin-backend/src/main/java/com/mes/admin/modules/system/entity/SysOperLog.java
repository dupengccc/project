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
 * 操作日志
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_oper_log")
public class SysOperLog extends BaseEntity {

    /** 工号（用户账号） */
    @Column(name = "emp_no", length = 64)
    private String empNo;

    /** 姓名（昵称/姓名） */
    @Column(name = "name", length = 128)
    private String name;

    /** 所属组织ID */
    @Column(name = "org_id")
    private Long orgId;

    /** 所属组织名称 */
    @Column(name = "org_name", length = 128)
    private String orgName;

    /** 模块 */
    @Column(name = "module", length = 128)
    private String module;

    /** 操作功能 */
    @Column(name = "operation", length = 128)
    private String operation;

    /** 方法/类名 */
    @Column(name = "method", length = 255)
    private String method;

    /** 请求方式（GET/POST/PUT/DELETE） */
    @Column(name = "request_method", length = 16)
    private String requestMethod;

    /** 请求 URL */
    @Column(name = "url", length = 255)
    private String url;

    /** 请求参数（JSON） */
    @Column(name = "params", columnDefinition = "TEXT")
    private String params;

    /** 客户端 IP */
    @Column(name = "ip", length = 64)
    private String ip;

    /** 操作地点 */
    @Column(name = "location", length = 255)
    private String location;

    /** 操作时间 */
    @Column(name = "oper_time")
    private Date operTime;

    /** 消耗时间（毫秒） */
    @Column(name = "cost")
    private Long cost;

    /** 是否正常 0=正常 1=异常 */
    @Column(name = "status")
    private Integer status;

    /** 异常信息 */
    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;
}
