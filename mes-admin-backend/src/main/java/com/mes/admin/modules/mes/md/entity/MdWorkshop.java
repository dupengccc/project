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
@Table(name = "mes_md_workshop")
public class MdWorkshop extends BaseEntity {

    @Column(length = 64)
    private String workshopCode;

    @Column(length = 128)
    private String workshopName;

    @Column(length = 32)
    private String leader;

    @Column(length = 32)
    private String phone;

    private Double area;

    private Integer status;
}
