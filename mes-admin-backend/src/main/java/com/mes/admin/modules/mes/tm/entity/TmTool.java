package com.mes.admin.modules.mes.tm.entity;

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
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mes_tm_tool")
public class TmTool extends BaseEntity {

    @Column(length = 64)
    private String toolCode;

    @Column(length = 128)
    private String toolName;

    @Column(length = 64)
    private String toolType;

    @Column(length = 128)
    private String spec;

    @Column(length = 128)
    private String workshopName;

    @Column(length = 128)
    private String storageLocation;

    @Column(length = 64)
    private String status;

    private Date inDate;
}
