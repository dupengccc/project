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
@Table(name = "mes_wm_location")
public class WmLocation extends BaseEntity {

    @Column(name = "location_code", length = 64)
    private String locationCode;

    @Column(name = "location_name", length = 128)
    private String locationName;

    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(name = "warehouse_name", length = 128)
    private String warehouseName;

    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "area_name", length = 128)
    private String areaName;

    @Column(name = "location_type", length = 32)
    private String locationType;

    @Column(name = "capacity")
    private Double capacity;

    @Column(name = "status")
    private Integer status;
}
