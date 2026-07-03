package com.mes.admin.modules.mes.qc.entity;

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
@Table(name = "mes_qc_record")
public class QcRecord extends BaseEntity {

    @Column(name = "record_code", length = 64)
    private String recordCode;

    @Column(name = "product_name", length = 128)
    private String productName;

    @Column(name = "check_qty")
    private Double checkQty;

    @Column(name = "qualified_qty")
    private Double qualifiedQty;

    @Column(name = "unqualified_qty")
    private Double unqualifiedQty;

    @Column(name = "check_result", length = 32)
    private String checkResult;

    @Column(name = "checker", length = 64)
    private String checker;

    @Column(name = "check_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkTime;

    @Column(name = "order_code", length = 64)
    private String orderCode;

    @Column(name = "status")
    private Integer status;
}
