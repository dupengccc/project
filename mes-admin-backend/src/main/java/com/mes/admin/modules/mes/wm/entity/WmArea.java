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
@Table(name = "mes_wm_area")
public class WmArea extends BaseEntity {

    @Column(length = 64)
    private String areaCode;

    @Column(length = 128)
    private String areaName;

    private Long warehouseId;

    @Column(length = 128)
    private String warehouseName;

    @Column(length = 32)
    private String areaType;

    private Double areaSize;

    private Integer status;
}
