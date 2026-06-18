package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user")
public class SysUser extends BaseEntity {

    @Column(length = 64, unique = true)
    private String username;

    @Column(length = 255)
    private String password;

    @Column(length = 64)
    private String nickname;

    @Column(length = 128)
    private String email;

    @Column(length = 32)
    private String phone;

    private Integer gender;

    @Column(length = 255)
    private String avatar;

    private Long deptId;

    private Integer status;

    @Column(length = 64)
    private String loginIp;

    private Date loginDate;

    private Date lastLoginDate;
}
