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
@Table(name = "mes_md_vendor")
public class MdVendor extends BaseEntity {

    @Column(name = "vendor_code", length = 64)
    private String vendorCode;

    @Column(name = "vendor_name", length = 128)
    private String vendorName;

    @Column(name = "contact", length = 32)
    private String contact;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "supply_level", length = 16)
    private String supplyLevel;

    @Column(name = "status")
    private Integer status;
}
