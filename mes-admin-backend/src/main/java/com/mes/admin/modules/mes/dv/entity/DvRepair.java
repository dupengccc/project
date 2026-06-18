package com.mes.admin.modules.mes.dv.entity;

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
@Table(name = "mes_dv_repair")
public class DvRepair extends BaseEntity {

    @Column(length = 64)
    private String repairCode;

    @Column(length = 64)
    private String deviceCode;

    @Column(length = 128)
    private String deviceName;

    @Column(length = 500)
    private String faultDesc;

    @Column(length = 64)
    private String reporter;

    private Date reportDate;

    @Column(length = 64)
    private String repairer;

    private Date repairDate;

    @Column(length = 64)
    private String repairStatus;

    private Double repairCost;

    private Integer status;
}
