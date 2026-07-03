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
@Table(name = "mes_md_process")
public class MdProcess extends BaseEntity {

    @Column(name = "process_code", length = 64)
    private String processCode;

    @Column(name = "process_name", length = 128)
    private String processName;

    @Column(name = "process_type", length = 16)
    private String processType;

    @Column(name = "standard_time")
    private Double standardTime;

    @Column(name = "status")
    private Integer status;
}
