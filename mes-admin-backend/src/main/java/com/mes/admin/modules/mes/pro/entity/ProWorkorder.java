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
@Table(name = "mes_pro_workorder")
public class ProWorkorder extends BaseEntity {

    @Column(length = 64)
    private String orderCode;

    @Column(length = 128)
    private String productName;

    private Double planQty;

    private Double completedQty;

    @Column(length = 32)
    private String status;

    private Integer priority;

    private Date planStart;

    private Date planEnd;

    private Date actualStart;

    private Date actualEnd;

    @Column(length = 64)
    private String workshopName;

    @Column(length = 64)
    private String workstationName;

    @Column(length = 64)
    private String operator;

    private Integer statusFlag;
}
