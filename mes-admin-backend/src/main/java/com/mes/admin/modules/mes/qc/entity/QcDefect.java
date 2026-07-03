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
@Table(name = "mes_qc_defect")
public class QcDefect extends BaseEntity {

    @Column(name = "defect_code", length = 64)
    private String defectCode;

    @Column(name = "defect_name", length = 128)
    private String defectName;

    @Column(name = "defect_type", length = 32)
    private String defectType;

    @Column(name = "severity", length = 32)
    private String severity;

    @Column(name = "suggestion", length = 500)
    private String suggestion;

    @Column(name = "status")
    private Integer status;
}
