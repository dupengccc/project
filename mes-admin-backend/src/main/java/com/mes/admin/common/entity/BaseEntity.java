package com.mes.admin.common.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedBy
    @Column(name = "create_by", length = 64, updatable = false)
    private String createBy;

    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    @LastModifiedBy
    @Column(name = "update_by", length = 64)
    private String updateBy;

    @LastModifiedDate
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "remark", length = 500)
    private String remark;
}
