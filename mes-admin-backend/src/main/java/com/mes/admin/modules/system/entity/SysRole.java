package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role")
public class SysRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64)
    private String roleName;

    @Column(length = 64)
    private String roleKey;

    private Integer roleSort;

    private Integer status;

    /**
     * 数据范围（数据权限类型）
     * 1 - 全部数据
     * 2 - 自定义数据（通过 sys_role_dept 关联）
     * 3 - 本部门数据
     * 4 - 本部门及以下
     * 5 - 仅本人数据
     */
    @Column(length = 2)
    private String dataScope;

    @Column(length = 255)
    private String remark;
}
