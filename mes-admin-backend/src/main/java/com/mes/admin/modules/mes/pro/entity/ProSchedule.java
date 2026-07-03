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
@Table(name = "mes_pro_schedule")
public class ProSchedule extends BaseEntity {

    @Column(name = "schedule_code", length = 64)
    private String scheduleCode;

    @Column(name = "order_code", length = 64)
    private String orderCode;

    @Column(name = "product_name", length = 128)
    private String productName;

    @Column(name = "workshop_name", length = 64)
    private String workshopName;

    @Column(name = "workstation_name", length = 64)
    private String workstationName;

    @Column(name = "schedule_date")
    private Date scheduleDate;

    @Column(name = "plan_qty")
    private Double planQty;

    @Column(name = "completed_qty")
    private Double completedQty;

    @Column(name = "status")
    private Integer status;
}
