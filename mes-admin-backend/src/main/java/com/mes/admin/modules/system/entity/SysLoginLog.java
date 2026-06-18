package com.mes.admin.modules.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 登录日志
 * 记录用户工号、姓名、IP、登录时间、退出时间、状态等
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "sys_login_log")
public class SysLoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 工号（用户账号） */
    @Column(length = 64)
    private String empNo;

    /** 姓名（昵称/姓名） */
    @Column(length = 128)
    private String name;

    /** 登录IP */
    @Column(length = 64)
    private String loginIp;

    /** 登录地点（可根据 IP 解析或留空） */
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

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
