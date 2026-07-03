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
    @Column(name = "material_code", length = 64)
    private String materialCode;

    /** 物料名称 */
    @Column(name = "material_name", length = 128)
    private String materialName;

    /** 规格型号 */
    @Column(name = "spec", length = 128)
    private String spec;

    /** 物料类型：原材料/半成品/成品/辅料 */
    @Column(name = "material_type", length = 32)
    private String materialType;

    /** 管理方式：自制/外购/委外加工 */
    @Column(name = "manage_mode", length = 32)
    private String manageMode;

    /** 单位 */
    @Column(name = "unit", length = 32)
    private String unit;

    /** 所属组织ID */
    @Column(name = "org_id")
    private Long orgId;

    /** 所属组织名称 */
    @Column(name = "org_name", length = 128)
    private String orgName;

    @Column(name = "safe_stock")
    private Double safeStock;

    @Column(name = "current_stock")
    private Double currentStock;

    /** 状态：0=启用 1=禁用 */
    @Column(name = "status")
    private Integer status;
}
