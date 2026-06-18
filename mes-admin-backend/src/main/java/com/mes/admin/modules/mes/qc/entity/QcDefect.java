package com.mes.admin.modules.mes.qc.entity;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_qc_defect")
public class QcDefect extends BaseEntity {

    @Column(length = 64)
    private String defectCode;

    @Column(length = 128)
    private String defectName;

    @Column(length = 32)
    private String defectType;

    @Column(length = 32)
    private String severity;

    @Column(length = 500)
    private String suggestion;

    private Integer status;
}
