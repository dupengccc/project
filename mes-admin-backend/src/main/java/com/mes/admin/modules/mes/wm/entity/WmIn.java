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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_wm_in")
public class WmIn extends BaseEntity {

    @Column(name = "in_code", length = 64)
    private String inCode;

    @Column(name = "material_name", length = 128)
    private String materialName;

    @Column(name = "in_qty")
    private Double inQty;

    @Column(name = "unit", length = 32)
    private String unit;

    @Column(name = "vendor_name", length = 128)
    private String vendorName;

    @Column(name = "warehouse_name", length = 128)
    private String warehouseName;

    @Column(name = "in_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inDate;

    @Column(name = "operator", length = 64)
    private String operator;

    @Column(name = "status")
    private Integer status;
}
