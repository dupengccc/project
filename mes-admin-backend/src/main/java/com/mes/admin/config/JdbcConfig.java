package com.mes.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * JdbcTemplate 配置
 * 提供原生 SQL 查询能力，配合 SqlQueryTemplate 使用
 */
@Configuration
public class JdbcConfig {

    @Resource
    private DataSource dataSource;

    /**
     * JdbcTemplate
     * 用于执行原生 SQL 查询
     */
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    /**
     * NamedParameterJdbcTemplate
     * 支持命名参数（:paramName）的 SQL 查询
     */
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
