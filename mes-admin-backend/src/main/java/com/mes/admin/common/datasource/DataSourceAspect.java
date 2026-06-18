package com.mes.admin.common.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据源切换 AOP 切面
 * 自动拦截带有 @DataSource 注解的方法，切换到对应的数据源
 */
@Aspect
@Component
@Order(0) // 确保在事务之前执行
public class DataSourceAspect {

    /**
     * 环绕通知：拦截所有带有 @DataSource 注解的方法
     */
    @Around("@annotation(com.mes.admin.common.datasource.DataSource) || @within(com.mes.admin.common.datasource.DataSource)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取目标方法或类上的注解
        String dataSourceKey = getDataSourceKey(point);
        if (dataSourceKey != null) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSourceKey);
        }
        try {
            return point.proceed();
        } finally {
            // 方法执行完毕后，清除数据源设置，恢复默认
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 获取数据源标识
     * 优先取方法上的注解，其次取类上的注解
     */
    private String getDataSourceKey(ProceedingJoinPoint point) {
        try {
            // 获取目标方法
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();

            // 优先取方法上的注解
            DataSource methodAnnotation = method.getAnnotation(DataSource.class);
            if (methodAnnotation != null) {
                return methodAnnotation.value();
            }

            // 其次取类上的注解
            DataSource classAnnotation = method.getDeclaringClass().getAnnotation(DataSource.class);
            if (classAnnotation != null) {
                return classAnnotation.value();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
