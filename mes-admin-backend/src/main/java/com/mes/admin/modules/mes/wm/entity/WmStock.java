package com.mes.admin.modules.mes.wm.entity;

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
@Table(name = "mes_wm_stock")
public class WmStock extends BaseEntity {

    @Column(name = "material_code", length = 64)
    private String materialCode;

    @Column(name = "material_name", length = 128)
    private String materialName;

    @Column(name = "spec", length = 128)
    private String spec;

    @Column(name = "warehouse_name", length = 128)
    private String warehouseName;

    @Column(name = "area_name", length = 128)
    private String areaName;

    @Column(name = "location_name", length = 128)
    private String locationName;

    @Column(name = "stock_qty")
    private Double stockQty;

    @Column(name = "safe_stock")
    private Double safeStock;

    @Column(name = "unit", length = 32)
    private String unit;

    @Column(name = "status")
    private Integer status;
}
