package com.mes.admin.modules.mes.dv.entity;

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
@Table(name = "mes_dv_repair")
public class DvRepair extends BaseEntity {

    @Column(name = "repair_code", length = 64)
    private String repairCode;

    @Column(name = "device_code", length = 64)
    private String deviceCode;

    @Column(name = "device_name", length = 128)
    private String deviceName;

    @Column(name = "fault_desc", length = 500)
    private String faultDesc;

    @Column(name = "reporter", length = 64)
    private String reporter;

    @Column(name = "report_date")
    private Date reportDate;

    @Column(name = "repairer", length = 64)
    private String repairer;

    @Column(name = "repair_date")
    private Date repairDate;

    @Column(name = "repair_status", length = 64)
    private String repairStatus;

    @Column(name = "repair_cost")
    private Double repairCost;

    @Column(name = "status")
    private Integer status;
}
