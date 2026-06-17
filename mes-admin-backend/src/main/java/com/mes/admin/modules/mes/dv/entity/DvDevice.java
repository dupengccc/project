package com.mes.admin.modules.mes.dv.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_dv_device")
public class DvDevice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64)
    private String deviceCode;

    @Column(length = 128)
    private String deviceName;

    @Column(length = 64)
    private String deviceType;

    @Column(length = 128)
    private String spec;

    private Long workshopId;

    @Column(length = 128)
    private String workshopName;

    @Column(length = 64)
    private String leader;

    private Date purchaseDate;

    private Integer serviceYears;

    private Integer status;
}
