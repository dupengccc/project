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
@Table(name = "mes_dv_device")
public class DvDevice extends BaseEntity {

    @Column(name = "device_code", length = 64)
    private String deviceCode;

    @Column(name = "device_name", length = 128)
    private String deviceName;

    @Column(name = "device_type", length = 64)
    private String deviceType;

    @Column(name = "spec", length = 128)
    private String spec;

    @Column(name = "workshop_id")
    private Long workshopId;

    @Column(name = "workshop_name", length = 128)
    private String workshopName;

    @Column(name = "leader", length = 64)
    private String leader;

    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Column(name = "service_years")
    private Integer serviceYears;

    @Column(name = "status")
    private Integer status;
}
