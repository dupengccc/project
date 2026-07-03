package com.mes.admin.modules.system.entity;

import com.mes.admin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 接口配置实体
 * 用于管理外部系统接口的配置信息
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_interface_config")
public class SysInterfaceConfig extends BaseEntity {

    /** 接口编码（唯一标识） */
    @Column(name = "interface_code", length = 64, unique = true)
    private String interfaceCode;

    /** 接口名称 */
    @Column(name = "interface_name", length = 128)
    private String interfaceName;

    /** 接口描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 接口地址（URL） */
    @Column(name = "url", length = 500)
    private String url;

    /** 请求方式（GET/POST/PUT/DELETE） */
    @Column(name = "request_method", length = 16)
    private String requestMethod;

    /** 内容类型（application/json/application/x-www-form-urlencoded/multipart/form-data） */
    @Column(name = "content_type", length = 64)
    private String contentType;

    /** 请求头配置（JSON格式） */
    @Column(name = "headers", columnDefinition = "TEXT")
    private String headers;

    /** 请求参数模板（JSON格式） */
    @Column(name = "param_template", columnDefinition = "TEXT")
    private String paramTemplate;

    /** 认证类型（none/basic/bearer/apiKey） */
    @Column(name = "auth_type", length = 32)
    private String authType;

    /** 认证配置（JSON格式，存储token/APIKey等） */
    @Column(name = "auth_config", columnDefinition = "TEXT")
    private String authConfig;

    /** 超时时间（毫秒） */
    @Column(name = "timeout")
    private Integer timeout;

    /** 重试次数 */
    @Column(name = "retry_count")
    private Integer retryCount;

    /** 重试间隔（毫秒） */
    @Column(name = "retry_interval")
    private Integer retryInterval;

    /** 调用频率限制（每分钟调用次数） */
    @Column(name = "rate_limit")
    private Integer rateLimit;

    /** 是否启用（0=启用 1=停用） */
    @Column(name = "status")
    private Integer status;

    /** 是否需要加密（0=否 1=是） */
    @Column(name = "encrypt_enabled")
    private Integer encryptEnabled;

    /** 加密密钥 */
    @Column(name = "encrypt_key", length = 255)
    private String encryptKey;

    /** 回调地址（用于异步接口） */
    @Column(name = "callback_url", length = 500)
    private String callbackUrl;

    /** 所属系统 */
    @Column(name = "owner_system", length = 64)
    private String ownerSystem;

    /** 联系人 */
    @Column(name = "contact", length = 64)
    private String contact;

    /** 联系电话 */
    @Column(name = "contact_phone", length = 32)
    private String contactPhone;

    /** 备注 */
    @Column(name = "remark", length = 500)
    private String remark;
}
