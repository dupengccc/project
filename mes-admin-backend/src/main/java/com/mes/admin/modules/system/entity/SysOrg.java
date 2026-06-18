package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_org")
public class SysOrg extends BaseEntity {

    private Long parentId;

    @Column(length = 64)
    private String orgCode;

    @Column(length = 128)
    private String name;

    @Column(length = 32)
    private String orgType;

    @Column(length = 64)
    private String leader;

    @Column(length = 32)
    private String phone;

    @Column(length = 128)
    private String email;

    @Column(length = 255)
    private String address;

    private Integer sort;

    private Integer status;
}
