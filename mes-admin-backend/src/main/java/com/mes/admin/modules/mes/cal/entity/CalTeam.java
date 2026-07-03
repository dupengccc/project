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
@Table(name = "mes_cal_team")
public class CalTeam extends BaseEntity {

    @Column(length = 64)
    private String teamCode;

    @Column(length = 128)
    private String teamName;

    @Column(length = 64)
    private String leader;

    @Column(length = 128)
    private String workshopName;

    private Integer memberCount;

    private Integer status;
}
