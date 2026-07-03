package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 生产单元实体
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_production_unit")
public class SysProductionUnit extends BaseEntity {

    /** 生产单元编码 */
    @Column(name = "unit_code", length = 64, unique = true)
    private String unitCode;

    /** 生产单元名称 */
    @Column(name = "unit_name", length = 128)
    private String unitName;

    /** 生产单元类型（产线/工作中心/工段等） */
    @Column(name = "unit_type", length = 32)
    private String unitType;

    /** 所属组织ID */
    @Column(name = "org_id")
    private Long orgId;

    /** 所属组织名称（冗余存储） */
    @Column(name = "org_name", length = 128)
    private String orgName;

    /** 状态（0启用 1停用） */
    @Column(name = "status")
    private Integer status;

    /** 排序号 */
    @Column(name = "sort")
    private Integer sort;
}
