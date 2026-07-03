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
 * 登录日志
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_login_log")
public class SysLoginLog extends BaseEntity {

    /** 工号（用户账号） */
    @Column(name = "emp_no", length = 64)
    private String empNo;

    /** 姓名（昵称/姓名） */
    @Column(name = "name", length = 128)
    private String name;

    /** 登录IP */
    @Column(name = "login_ip", length = 64)
    private String loginIp;

    /** 登录地点 */
    @Column(name = "login_location", length = 255)
    private String loginLocation;

    /** 浏览器/UA */
    @Column(name = "browser", length = 255)
    private String browser;

    /** 操作系统 */
    @Column(name = "os", length = 128)
    private String os;

    /** 登录时间 */
    @Column(name = "login_time")
    private Date loginTime;

    /** 退出时间 */
    @Column(name = "logout_time")
    private Date logoutTime;

    /** 登录状态 0=成功 1=失败 */
    @Column(name = "status")
    private Integer status;

    /** 消息（如"登录成功"、"密码错误"等） */
    @Column(name = "msg", length = 512)
    private String msg;

    /** 所属组织ID */
    @Column(name = "org_id")
    private Long orgId;

    /** 所属组织名称 */
    @Column(name = "org_name", length = 128)
    private String orgName;
}
