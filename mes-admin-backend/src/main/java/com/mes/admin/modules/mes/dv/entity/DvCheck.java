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

    @Column(length = 64)
    private String checkCode;

    @Column(length = 64)
    private String deviceCode;

    @Column(length = 128)
    private String deviceName;

    @Column(length = 64)
    private String checkType;

    @Column(length = 64)
    private String checker;

    private Date checkDate;

    @Column(length = 64)
    private String checkResult;

    private Date nextCheckDate;

    private Integer status;
}
