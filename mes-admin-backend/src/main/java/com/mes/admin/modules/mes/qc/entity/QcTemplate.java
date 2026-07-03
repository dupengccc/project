package com.mes.admin.modules.mes.qc.entity;

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
@Table(name = "mes_qc_template")
public class QcTemplate extends BaseEntity {

    @Column(length = 64)
    private String templateCode;

    @Column(length = 128)
    private String templateName;

    @Column(length = 128)
    private String productType;

    private Integer checkItemCount;

    @Column(length = 32)
    private String version;

    private Integer status;
}
