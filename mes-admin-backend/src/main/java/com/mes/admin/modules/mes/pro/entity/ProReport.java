package com.mes.admin.modules.mes.pro.entity;

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
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_pro_report")
public class ProReport extends BaseEntity {

    @Column(length = 64)
    private String reportCode;

    @Column(length = 64)
    private String orderCode;

    @Column(length = 128)
    private String productName;

    @Column(length = 64)
    private String processName;

    @Column(length = 64)
    private String operator;

    private Double reportQty;

    private Double badQty;

    private Date reportTime;

    private Integer status;
}
