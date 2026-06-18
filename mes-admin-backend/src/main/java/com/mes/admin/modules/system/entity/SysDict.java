package com.mes.admin.modules.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 数据字典（父子层级结构）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "sys_dict")
public class SysDict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    /** 备注 */
    @Column(length = 512)
    private String remark;

    /** 状态 0=激活 1=停用 */
    private Integer status;

    /** 创建人 */
    @Column(length = 64)
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新人 */
    @Column(length = 64)
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;
}
