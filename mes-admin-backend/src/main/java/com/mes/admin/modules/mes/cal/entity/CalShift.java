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

    @Column(name = "shift_code", length = 64)
    private String shiftCode;

    @Column(name = "shift_name", length = 128)
    private String shiftName;

    @Column(name = "start_time", length = 16)
    private String startTime;

    @Column(name = "end_time", length = 16)
    private String endTime;

    @Column(name = "hours")
    private Double hours;

    @Column(name = "shift_type", length = 64)
    private String shiftType;

    @Column(name = "status")
    private Integer status;
}
