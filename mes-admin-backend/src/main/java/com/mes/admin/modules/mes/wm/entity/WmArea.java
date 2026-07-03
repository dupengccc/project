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
@Table(name = "mes_wm_area")
public class WmArea extends BaseEntity {

    @Column(name = "area_code", length = 64)
    private String areaCode;

    @Column(name = "area_name", length = 128)
    private String areaName;

    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(name = "warehouse_name", length = 128)
    private String warehouseName;

    @Column(name = "area_type", length = 32)
    private String areaType;

    @Column(name = "area_size")
    private Double areaSize;

    @Column(name = "status")
    private Integer status;
}
