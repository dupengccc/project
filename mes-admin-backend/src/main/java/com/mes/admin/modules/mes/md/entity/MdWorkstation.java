package com.mes.admin.modules.mes.md.entity;

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
@Table(name = "mes_md_workstation")
public class MdWorkstation extends BaseEntity {

    @Column(name = "station_code", length = 64)
    private String stationCode;

    @Column(name = "station_name", length = 128)
    private String stationName;

    @Column(name = "workshop_id")
    private Long workshopId;

    @Column(name = "workshop_name", length = 128)
    private String workshopName;

    @Column(name = "device_no", length = 64)
    private String deviceNo;

    @Column(name = "operator", length = 32)
    private String operator;

    @Column(name = "status")
    private Integer status;
}
