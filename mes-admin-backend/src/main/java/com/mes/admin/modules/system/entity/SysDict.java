package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 数据字典（父子层级结构）
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_dict")
public class SysDict extends BaseEntity {

    /** 字典名称 */
    @Column(length = 128)
    private String dictName;

    /** 字典编码（唯一） */
    @Column(length = 128)
    private String dictCode;

    /** 父节点ID（0=根节点） */
    private Long parentId;

    /** 父节点编码（可为空，根节点用） */
    @Column(length = 128)
    private String parentCode;

    /** 字典值（供下拉框使用，可存 id 或 code） */
    @Column(length = 128)
    private String dictValue;

    /** 排序号 */
    private Integer sort;

    /** 状态 0=激活 1=停用 */
    private Integer status;
}
