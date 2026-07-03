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
@Table(name = "mes_md_customer")
public class MdCustomer extends BaseEntity {

    @Column(name = "customer_code", length = 64)
    private String customerCode;

    @Column(name = "customer_name", length = 128)
    private String customerName;

    @Column(name = "contact", length = 32)
    private String contact;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "credit_level", length = 8)
    private String creditLevel;

    @Column(name = "status")
    private Integer status;
}
