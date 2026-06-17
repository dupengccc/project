package com.mes.admin.modules.mes.pro.entity;

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
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_pro_schedule")
public class ProSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64)
    private String scheduleCode;

    @Column(length = 64)
    private String orderCode;

    @Column(length = 128)
    private String productName;

    @Column(length = 64)
    private String workshopName;

    @Column(length = 64)
    private String workstationName;

    private Date scheduleDate;

    private Double planQty;

    private Double completedQty;

    private Integer status;
}
