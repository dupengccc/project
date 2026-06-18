package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 角色-组织 关联表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role_dept")
public class SysRoleDept extends BaseEntity {

    /** 角色ID */
    private Long roleId;

    /** 组织（部门）ID */
    private Long deptId;
}
