package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 角色-组织 关联表
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role_dept")
public class SysRoleDept extends BaseEntity {

    /** 角色ID */
    @Column(name = "role_id")
    private Long roleId;

    /** 组织（部门）ID */
    @Column(name = "dept_id")
    private Long deptId;
}
