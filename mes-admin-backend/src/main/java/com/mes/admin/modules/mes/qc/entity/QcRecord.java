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

    @Column(length = 64)
    private String recordCode;

    @Column(length = 128)
    private String productName;

    private Double checkQty;

    private Double qualifiedQty;

    private Double unqualifiedQty;

    @Column(length = 32)
    private String checkResult;

    @Column(length = 64)
    private String checker;

    @Temporal(TemporalType.TIMESTAMP)
    private Date checkTime;

    @Column(length = 64)
    private String orderCode;

    private Integer status;
}
