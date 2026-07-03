package com.mes.admin.common.entity;

import com.mes.admin.common.util.IdGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.Date;

/**
 * 基础实体类
 * 所有业务实体继承此类，统一管理：
 * - id: 20位数字（由 IdGenerator 自动生成）
 * - createBy / createTime: 创建人、创建时间
 * - updateBy / updateTime: 更新人、更新时间
 * - remark: 备注
 *
 * 使用 @SuperBuilder 支持继承链上的 Builder 模式，
 * 子类需同步标注 @SuperBuilder 才能使用 builder 构建父类字段。
 */
@Data
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * 主键ID：20位数字，格式为 yyyyMMddHHmmssSSS + 3位序号
     * 新增时由 @PrePersist 自动调用 IdGenerator.generateId() 生成
     */
    @Id
    @Column(name = "id", precision = 20)
    private Long id;

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

    /**
     * 新增前自动生成ID
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = IdGenerator.generateId();
        }
        if (this.createTime == null) {
            this.createTime = new Date();
        }
    }
}
