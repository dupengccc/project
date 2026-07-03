package com.mes.admin.common.datasource;

import java.lang.annotation.*;

/**
 * 数据源切换注解
 * 标注在方法或类上，指定使用的数据源
 *
 * 用法：
 * 1. 方法级别：@DataSource("master") / @DataSource("slave")
 * 2. 类级别：标注后类内所有方法都使用指定数据源
 *
 * 默认数据源为 master
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    /**
     * 数据源名称
     * - master: 主数据源（默认）
     * - slave: 从数据源（读写分离场景）
     */
    String value() default "master";
}
