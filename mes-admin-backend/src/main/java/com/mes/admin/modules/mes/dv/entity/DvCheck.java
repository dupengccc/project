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
@Table(name = "mes_dv_check")
public class DvCheck extends BaseEntity {

    @Column(name = "check_code", length = 64)
    private String checkCode;

    @Column(name = "device_code", length = 64)
    private String deviceCode;

    @Column(name = "device_name", length = 128)
    private String deviceName;

    @Column(name = "check_type", length = 64)
    private String checkType;

    @Column(name = "checker", length = 64)
    private String checker;

    @Column(name = "check_date")
    private Date checkDate;

    @Column(name = "check_result", length = 64)
    private String checkResult;

    @Column(name = "next_check_date")
    private Date nextCheckDate;

    @Column(name = "status")
    private Integer status;
}
