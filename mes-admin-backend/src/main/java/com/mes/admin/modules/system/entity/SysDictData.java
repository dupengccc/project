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

    @Column(name = "dict_sort")
    private Integer dictSort;

    @Column(name = "dict_label", length = 128)
    private String dictLabel;

    @Column(name = "dict_value", length = 128)
    private String dictValue;

    @Column(name = "dict_type", length = 128)
    private String dictType;

    @Column(name = "css_class", length = 128)
    private String cssClass;

    @Column(name = "list_class", length = 128)
    private String listClass;

    @Column(name = "is_default", length = 8)
    private String isDefault;

    @Column(name = "status")
    private Integer status;
}
