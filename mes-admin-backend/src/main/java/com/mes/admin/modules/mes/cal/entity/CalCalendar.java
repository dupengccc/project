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
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_cal_calendar")
public class CalCalendar extends BaseEntity {

    @Column(name = "calendar_code", length = 64)
    private String calendarCode;

    @Column(name = "calendar_date")
    private Date calendarDate;

    @Column(name = "team_name", length = 128)
    private String teamName;

    @Column(name = "shift_name", length = 128)
    private String shiftName;

    @Column(name = "member_names", length = 500)
    private String memberNames;

    @Column(name = "member_count")
    private Integer memberCount;
}
