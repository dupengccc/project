package com.mes.admin.modules.mes.wm.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "mes_wm_in")
public class WmIn extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64)
    private String inCode;

    @Column(length = 128)
    private String materialName;

    private Double inQty;

    @Column(length = 32)
    private String unit;

    @Column(length = 128)
    private String vendorName;

    @Column(length = 128)
    private String warehouseName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date inDate;

    @Column(length = 64)
    private String operator;

    private Integer status;
}
