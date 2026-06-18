package com.mes.admin.modules.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 角色-物料 关联表
 * 用于数据权限中控制角色可访问的物料范围
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "sys_role_material")
public class SysRoleMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 角色ID */
    private Long roleId;

    /** 物料ID */
    private Long materialId;

    /** 物料编码（冗余存储，便于查询） */
    @Column(length = 64)
    private String materialCode;

    /** 物料名称（冗余存储，便于查询） */
    @Column(length = 128)
    private String materialName;
}
