package com.mes.admin.modules.mes.wm.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_wm_stock")
public class WmStock extends BaseEntity {

    @Column(length = 64)
    private String materialCode;

    @Column(length = 128)
    private String materialName;

    @Column(length = 128)
    private String spec;

    @Column(length = 128)
    private String warehouseName;

    @Column(length = 128)
    private String areaName;

    @Column(length = 128)
    private String locationName;

    private Double stockQty;

    private Double safeStock;

    @Column(length = 32)
    private String unit;

    private Integer status;
}
