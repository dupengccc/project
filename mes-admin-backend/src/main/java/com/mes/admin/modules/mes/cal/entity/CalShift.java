package com.mes.admin.modules.mes.cal.entity;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_cal_shift")
public class CalShift extends BaseEntity {

    @Column(length = 64)
    private String shiftCode;

    @Column(length = 128)
    private String shiftName;

    @Column(length = 16)
    private String startTime;

    @Column(length = 16)
    private String endTime;

    private Double hours;

    @Column(length = 64)
    private String shiftType;

    private Integer status;
}
