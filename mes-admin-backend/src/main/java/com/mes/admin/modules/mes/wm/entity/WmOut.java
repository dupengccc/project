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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_wm_out")
public class WmOut extends BaseEntity {

    @Column(length = 64)
    private String outCode;

    @Column(length = 128)
    private String materialName;

    private Double outQty;

    @Column(length = 32)
    private String unit;

    @Column(length = 128)
    private String receiveDept;

    @Column(length = 128)
    private String warehouseName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date outDate;

    @Column(length = 64)
    private String operator;

    private Integer status;
}
