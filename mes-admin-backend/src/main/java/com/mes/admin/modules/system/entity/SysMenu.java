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
@Table(name = "sys_menu")
public class SysMenu extends BaseEntity {

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "menu_name", length = 64)
    private String menuName;

    @Column(name = "path", length = 255)
    private String path;

    @Column(name = "component", length = 255)
    private String component;

    @Column(name = "query", length = 255)
    private String query;

    @Column(name = "route_name", length = 64)
    private String routeName;

    @Column(name = "is_frame")
    private Integer isFrame;

    @Column(name = "is_cache")
    private Integer isCache;

    @Column(name = "menu_type", length = 8)
    private String menuType;

    @Column(name = "visible", length = 8)
    private String visible;

    @Column(name = "status")
    private Integer status;

    @Column(name = "perms", length = 128)
    private String perms;

    @Column(name = "icon", length = 64)
    private String icon;

    @Column(name = "sort")
    private Integer sort;
}
