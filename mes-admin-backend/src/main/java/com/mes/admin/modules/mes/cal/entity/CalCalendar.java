package com.mes.admin.modules.mes.cal.entity;

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
@Table(name = "mes_cal_calendar")
public class CalCalendar extends BaseEntity {

    @Column(length = 64)
    private String calendarCode;

    private Date calendarDate;

    @Column(length = 128)
    private String teamName;

    @Column(length = 128)
    private String shiftName;

    @Column(length = 500)
    private String memberNames;

    private Integer memberCount;
}
