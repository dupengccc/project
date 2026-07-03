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
@Table(name = "mes_wm_warehouse")
public class WmWarehouse extends BaseEntity {

    @Column(name = "warehouse_code", length = 64)
    private String warehouseCode;

    @Column(name = "warehouse_name", length = 128)
    private String warehouseName;

    @Column(name = "warehouse_type", length = 32)
    private String warehouseType;

    @Column(name = "leader", length = 64)
    private String leader;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "status")
    private Integer status;
}
