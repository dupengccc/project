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

    @Column(length = 64)
    private String locationCode;

    @Column(length = 128)
    private String locationName;

    private Long warehouseId;

    @Column(length = 128)
    private String warehouseName;

    private Long areaId;

    @Column(length = 128)
    private String areaName;

    @Column(length = 32)
    private String locationType;

    private Double capacity;

    private Integer status;
}
