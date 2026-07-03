package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_org")
public class SysOrg extends BaseEntity {

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "org_code", length = 64)
    private String orgCode;

    @Column(name = "name", length = 128)
    private String name;

    @Column(name = "org_type", length = 32)
    private String orgType;

    @Column(name = "leader", length = 64)
    private String leader;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "status")
    private Integer status;
}
