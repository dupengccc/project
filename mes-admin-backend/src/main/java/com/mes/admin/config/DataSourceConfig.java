package com.mes.admin.config;

import com.mes.admin.common.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String masterUrl;

    @Value("${spring.datasource.dynamic.datasource.master.username}")
    private String masterUsername;

    @Value("${spring.datasource.dynamic.datasource.master.password}")
    private String masterPassword;

    @Value("${spring.datasource.dynamic.datasource.master.driver-class-name}")
    private String masterDriver;

    @Value("${spring.datasource.dynamic.datasource.slave.url}")
    private String slaveUrl;

    @Value("${spring.datasource.dynamic.datasource.slave.username}")
    private String slaveUsername;

    @Value("${spring.datasource.dynamic.datasource.slave.password}")
    private String slavePassword;

    @Value("${spring.datasource.dynamic.datasource.slave.driver-class-name}")
    private String slaveDriver;

    @Value("${spring.datasource.dynamic.datasource.mes.url}")
    private String mesUrl;

    @Value("${spring.datasource.dynamic.datasource.mes.username}")
    private String mesUsername;

    @Value("${spring.datasource.dynamic.datasource.mes.password}")
    private String mesPassword;

    @Value("${spring.datasource.dynamic.datasource.mes.driver-class-name}")
    private String mesDriver;

    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minIdle;

    @Value("${spring.datasource.hikari.maximum-pool-size:20}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.idle-timeout:30000}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:60000}")
    private long maxLifetime;

    private HikariDataSource build(String driver, String url, String username, String password) {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(driver);
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMinimumIdle(minIdle);
        ds.setMaximumPoolSize(maxPoolSize);
        ds.setIdleTimeout(idleTimeout);
        ds.setConnectionTimeout(connectionTimeout);
        ds.setMaxLifetime(maxLifetime);
        return ds;
    }

    @Bean("masterDataSource")
    public DataSource masterDataSource() {
        return build(masterDriver, masterUrl, masterUsername, masterPassword);
    }

    @Bean("slaveDataSource")
    public DataSource slaveDataSource() {
        return build(slaveDriver, slaveUrl, slaveUsername, slavePassword);
    }

    @Bean("mesDataSource")
    public DataSource mesDataSource() {
        return build(mesDriver, mesUrl, mesUsername, mesPassword);
    }

    @Bean
    @Primary
    public DataSource dynamicDataSource(@Qualifier("masterDataSource") DataSource master,
                                        @Qualifier("slaveDataSource") DataSource slave,
                                        @Qualifier("mesDataSource") DataSource mes) {
        DynamicDataSource dynamic = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", master);
        targetDataSources.put("slave", slave);
        targetDataSources.put("mes", mes);
        dynamic.setTargetDataSources(targetDataSources);
        dynamic.setDefaultTargetDataSource(master);
        return dynamic;
    }
}
