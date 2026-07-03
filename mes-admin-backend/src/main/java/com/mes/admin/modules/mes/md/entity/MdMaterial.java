package com.mes.admin.modules.mes.md.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_md_material")
public class MdMaterial extends BaseEntity {

    /** 物料编码 */
    @Column(length = 64)
    private String materialCode;

    /** 物料名称 */
    @Column(length = 128)
    private String materialName;

    /** 规格型号 */
    @Column(length = 128)
    private String spec;

    /** 物料类型：原材料/半成品/成品/辅料 */
    @Column(length = 32)
    private String materialType;

    /** 管理方式：自制/外购/委外加工 */
    @Column(length = 32)
    private String manageMode;

    /** 单位 */
    @Column(length = 32)
    private String unit;

    /** 所属组织ID */
    private Long orgId;

    /** 所属组织名称 */
    @Column(length = 128)
    private String orgName;

    private Double safeStock;

    private Double currentStock;

    /** 状态：0=启用 1=禁用 */
    private Integer status;
}
