package com.mes.admin.common.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(1)
public class DynamicDataSourceAspect {

    @Pointcut("@annotation(com.mes.admin.common.datasource.DataSource) " +
            "|| @within(com.mes.admin.common.datasource.DataSource)")
    public void dataSourcePointCut() {
    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        com.mes.admin.common.datasource.DataSource dataSource = AnnotationUtils.findAnnotation(
                method, com.mes.admin.common.datasource.DataSource.class);
        if (dataSource == null) {
            dataSource = AnnotationUtils.findAnnotation(point.getTarget().getClass(),
                    com.mes.admin.common.datasource.DataSource.class);
        }

        if (dataSource != null) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value());
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }
}
