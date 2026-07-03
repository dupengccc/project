package com.mes.admin.modules.mes.pro.entity;

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
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_pro_report")
public class ProReport extends BaseEntity {

    @Column(name = "report_code", length = 64)
    private String reportCode;

    @Column(name = "order_code", length = 64)
    private String orderCode;

    @Column(name = "product_name", length = 128)
    private String productName;

    @Column(name = "process_name", length = 64)
    private String processName;

    @Column(name = "operator", length = 64)
    private String operator;

    @Column(name = "report_qty")
    private Double reportQty;

    @Column(name = "bad_qty")
    private Double badQty;

    @Column(name = "report_time")
    private Date reportTime;

    @Column(name = "status")
    private Integer status;
}
