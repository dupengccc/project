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
@Table(name = "mes_md_customer")
public class MdCustomer extends BaseEntity {

    @Column(length = 64)
    private String customerCode;

    @Column(length = 128)
    private String customerName;

    @Column(length = 32)
    private String contact;

    @Column(length = 32)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 8)
    private String creditLevel;

    private Integer status;
}
