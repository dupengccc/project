package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 角色-物料 关联表
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role_material")
public class SysRoleMaterial extends BaseEntity {

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
