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
@Table(name = "mes_cal_plan")
public class CalPlan extends BaseEntity {

    @Column(name = "plan_code", length = 64)
    private String planCode;

    @Column(name = "team_name", length = 128)
    private String teamName;

    @Column(name = "plan_date")
    private Date planDate;

    @Column(name = "shift_name", length = 128)
    private String shiftName;

    @Column(name = "member_count")
    private Integer memberCount;

    @Column(name = "creator", length = 64)
    private String creator;

    @Column(name = "status")
    private Integer status;
}
