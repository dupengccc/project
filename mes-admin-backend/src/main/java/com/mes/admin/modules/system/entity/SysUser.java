package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user")
public class SysUser extends BaseEntity {

    @Column(name = "username", length = 64, unique = true)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "nickname", length = 64)
    private String nickname;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "dept_id")
    private Long deptId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "login_ip", length = 64)
    private String loginIp;

    @Column(name = "login_date")
    private Date loginDate;

    @Column(name = "last_login_date")
    private Date lastLoginDate;
}
