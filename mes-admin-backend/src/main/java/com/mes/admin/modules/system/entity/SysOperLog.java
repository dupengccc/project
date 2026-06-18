package com.mes.admin.modules.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 操作日志
 * 记录用户工号、姓名、模块、操作、创建时间、是否正常、异常信息等
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "sys_oper_log")
public class SysOperLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 工号（用户账号） */
    @Column(length = 64)
    private String empNo;

    /** 姓名（昵称/姓名） */
    @Column(length = 128)
    private String name;

    /** 所属组织ID */
    private Long orgId;

    /** 所属组织名称 */
    @Column(length = 128)
    private String orgName;

    /** 模块 */
    @Column(length = 128)
    private String module;

    /** 操作功能 */
    @Column(length = 128)
    private String operation;

    /** 方法/类名 */
    @Column(length = 255)
    private String method;

    /** 请求方式（GET/POST/PUT/DELETE） */
    @Column(length = 16)
    private String requestMethod;

    /** 请求 URL */
    @Column(length = 255)
    private String url;

    /** 请求参数（JSON） */
    @Column(columnDefinition = "TEXT")
    private String params;

    /** 客户端 IP */
    @Column(length = 64)
    private String ip;

    /** 操作地点 */
    @Column(length = 255)
    private String location;

    /** 操作时间 */
    private Date operTime;

    /** 消耗时间（毫秒） */
    private Long cost;

    /** 是否正常 0=正常 1=异常 */
    private Integer status;

    /** 异常信息 */
    @Column(columnDefinition = "TEXT")
    private String errorMsg;

    /** 创建时间 */
    private Date createTime;
}
