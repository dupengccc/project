package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_dict_data")
public class SysDictData extends BaseEntity {

    private Integer dictSort;

    @Column(length = 128)
    private String dictLabel;

    @Column(length = 128)
    private String dictValue;

    @Column(length = 128)
    private String dictType;

    @Column(length = 128)
    private String cssClass;

    @Column(length = 128)
    private String listClass;

    @Column(length = 8)
    private String isDefault;

    private Integer status;
}
