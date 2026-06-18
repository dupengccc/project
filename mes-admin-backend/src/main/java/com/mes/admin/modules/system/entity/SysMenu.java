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
@Table(name = "sys_menu")
public class SysMenu extends BaseEntity {

    private Long parentId;

    @Column(length = 64)
    private String menuName;

    @Column(length = 255)
    private String path;

    @Column(length = 255)
    private String component;

    @Column(length = 255)
    private String query;

    @Column(length = 64)
    private String routeName;

    private Integer isFrame;

    private Integer isCache;

    @Column(length = 8)
    private String menuType;

    @Column(length = 8)
    private String visible;

    private Integer status;

    @Column(length = 128)
    private String perms;

    @Column(length = 64)
    private String icon;

    private Integer sort;
}
