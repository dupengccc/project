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

    @Column(name = "template_code", length = 64)
    private String templateCode;

    @Column(name = "template_name", length = 128)
    private String templateName;

    @Column(name = "product_type", length = 128)
    private String productType;

    @Column(name = "check_item_count")
    private Integer checkItemCount;

    @Column(name = "version", length = 32)
    private String version;

    @Column(name = "status")
    private Integer status;
}
