package com.mes.admin.modules.mes.md.entity;

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
@Table(name = "mes_md_bom")
public class MdBom extends BaseEntity {

    @Column(length = 64)
    private String bomCode;

    @Column(length = 128)
    private String bomName;

    @Column(length = 128)
    private String productName;

    @Column(length = 32)
    private String version;

    private Integer status;
}
