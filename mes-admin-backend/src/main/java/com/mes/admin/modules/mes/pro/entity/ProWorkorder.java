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
@Table(name = "mes_pro_workorder")
public class ProWorkorder extends BaseEntity {

    @Column(name = "order_code", length = 64)
    private String orderCode;

    @Column(name = "product_name", length = 128)
    private String productName;

    @Column(name = "plan_qty")
    private Double planQty;

    @Column(name = "completed_qty")
    private Double completedQty;

    @Column(name = "status", length = 32)
    private String status;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "plan_start")
    private Date planStart;

    @Column(name = "plan_end")
    private Date planEnd;

    @Column(name = "actual_start")
    private Date actualStart;

    @Column(name = "actual_end")
    private Date actualEnd;

    @Column(name = "workshop_name", length = 64)
    private String workshopName;

    @Column(name = "workstation_name", length = 64)
    private String workstationName;

    @Column(name = "operator", length = 64)
    private String operator;

    @Column(name = "status_flag")
    private Integer statusFlag;
}
