package com.mes.admin.modules.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 角色-组织 关联表
 * 用于"自定义数据范围"场景下，指定某个角色可见的组织节点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "sys_role_dept")
public class SysRoleDept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 角色ID */
    private Long roleId;

    /** 组织（部门）ID */
    private Long deptId;
}
