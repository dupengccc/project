package com.mes.admin.modules.mes.tm.entity;

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
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_tm_tool")
public class TmTool extends BaseEntity {

    @Column(name = "tool_code", length = 64)
    private String toolCode;

    @Column(name = "tool_name", length = 128)
    private String toolName;

    @Column(name = "tool_type", length = 64)
    private String toolType;

    @Column(name = "spec", length = 128)
    private String spec;

    @Column(name = "workshop_name", length = 128)
    private String workshopName;

    @Column(name = "storage_location", length = 128)
    private String storageLocation;

    @Column(name = "status", length = 64)
    private String status;

    @Column(name = "in_date")
    private Date inDate;
}
