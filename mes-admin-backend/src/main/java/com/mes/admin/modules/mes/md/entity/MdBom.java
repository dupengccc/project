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
@Table(name = "mes_md_bom")
public class MdBom extends BaseEntity {

    @Column(name = "bom_code", length = 64)
    private String bomCode;

    @Column(name = "bom_name", length = 128)
    private String bomName;

    @Column(name = "product_name", length = 128)
    private String productName;

    @Column(name = "version", length = 32)
    private String version;

    @Column(name = "status")
    private Integer status;
}
