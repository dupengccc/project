package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 登录日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_login_log")
public class SysLoginLog extends BaseEntity {

    /** 工号（用户账号） */
    @Column(length = 64)
    private String empNo;

    /** 姓名（昵称/姓名） */
    @Column(length = 128)
    private String name;

    /** 登录IP */
    @Column(length = 64)
    private String loginIp;

    /** 登录地点 */
    @Column(length = 255)
    private String loginLocation;

    /** 浏览器/UA */
    @Column(length = 255)
    private String browser;

    /** 操作系统 */
    @Column(length = 128)
    private String os;

    /** 登录时间 */
    private Date loginTime;

    /** 退出时间 */
    private Date logoutTime;

    /** 登录状态 0=成功 1=失败 */
    private Integer status;

    /** 消息（如"登录成功"、"密码错误"等） */
    @Column(length = 512)
    private String msg;

    /** 所属组织ID */
    private Long orgId;

    /** 所属组织名称 */
    @Column(length = 128)
    private String orgName;
}
