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
@Table(name = "mes_md_route")
public class MdRoute extends BaseEntity {

    @Column(name = "route_code", length = 64)
    private String routeCode;

    @Column(name = "route_name", length = 128)
    private String routeName;

    @Column(name = "product_name", length = 128)
    private String productName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "process_count")
    private Integer processCount;

    @Column(name = "status")
    private Integer status;
}
