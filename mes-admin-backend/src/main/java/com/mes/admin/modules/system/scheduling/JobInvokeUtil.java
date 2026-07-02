package com.mes.admin.modules.system.scheduling;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 任务执行反射工具类
 * <p>
 * 解析调用目标字符串，通过 Spring 容器获取 Bean 并反射调用方法。
 * 支持格式：
 * <ul>
 *   <li>beanName.method</li>
 *   <li>beanName.method('str1','str2')</li>
 *   <li>beanName.method(123, true)</li>
 * </ul>
 */
@Component
public class JobInvokeUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JobInvokeUtil.applicationContext = applicationContext;
    }

    /**
     * 执行方法
     *
     * @param invokeTarget 调用目标字符串
     */
    public static void invokeMethod(String invokeTarget) throws Exception {
        if (!StringUtils.hasText(invokeTarget)) {
            throw new IllegalArgumentException("调用目标不能为空");
        }
        String beanName = getBeanName(invokeTarget);
        String methodName = getMethodName(invokeTarget);
        List<Object[]> methodParams = getMethodParams(invokeTarget);

        if (!applicationContext.containsBean(beanName)) {
            throw new IllegalArgumentException("Bean不存在：" + beanName);
        }
        Object bean = applicationContext.getBean(beanName);

        // 含参方法
        if (!methodParams.isEmpty()) {
            Class<?>[] paramTypes = new Class[methodParams.size()];
            Object[] paramValues = new Object[methodParams.size()];
            for (int i = 0; i < methodParams.size(); i++) {
                paramTypes[i] = (Class<?>) methodParams.get(i)[0];
                paramValues[i] = methodParams.get(i)[1];
            }
            Method method = ReflectionUtils.findMethod(bean.getClass(), methodName, paramTypes);
            if (method == null) {
                throw new NoSuchMethodException("方法不存在：" + methodName);
            }
            invoke(bean, method, paramValues);
        } else {
            // 无参方法
            Method method = ReflectionUtils.findMethod(bean.getClass(), methodName);
            if (method == null) {
                throw new NoSuchMethodException("方法不存在：" + methodName);
            }
            invoke(bean, method);
        }
    }

    private static void invoke(Object bean, Method method, Object... args) throws Exception {
        try {
            ReflectionUtils.makeAccessible(method);
            method.invoke(bean, args);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessException("方法访问异常：" + e.getMessage());
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if (target instanceof Exception) {
                throw (Exception) target;
            }
            throw e;
        }
    }

    /** 获取bean名称 */
    private static String getBeanName(String invokeTarget) {
        String target = invokeTarget.trim();
        int dotIndex = target.indexOf('.');
        if (dotIndex <= 0) {
            throw new IllegalArgumentException("调用目标格式错误，应为 beanName.method(params)：" + invokeTarget);
        }
        return target.substring(0, dotIndex).trim();
    }

    /** 获取方法名称 */
    private static String getMethodName(String invokeTarget) {
        String target = invokeTarget.trim();
        int dotIndex = target.indexOf('.');
        String methodPart = target.substring(dotIndex + 1);
        int parenIndex = methodPart.indexOf('(');
        if (parenIndex > 0) {
            return methodPart.substring(0, parenIndex).trim();
        }
        return methodPart.trim();
    }

    /** 解析方法参数 */
    private static List<Object[]> getMethodParams(String invokeTarget) {
        String target = invokeTarget.trim();
        int start = target.indexOf('(');
        int end = target.lastIndexOf(')');
        if (start < 0 || end < 0 || end <= start) {
            return new ArrayList<>();
        }
        String paramStr = target.substring(start + 1, end).trim();
        if (paramStr.isEmpty()) {
            return new ArrayList<>();
        }
        String[] params = splitParams(paramStr);
        List<Object[]> result = new LinkedList<>();
        for (String param : params) {
            String p = param.trim();
            Object[] typed = parseParam(p);
            result.add(typed);
        }
        return result;
    }

    /** 按逗号拆分参数（支持字符串内含逗号的情况） */
    private static String[] splitParams(String paramStr) {
        List<String> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inString = false;
        for (int i = 0; i < paramStr.length(); i++) {
            char c = paramStr.charAt(i);
            if (c == '\'') {
                inString = !inString;
                sb.append(c);
            } else if (c == ',' && !inString) {
                params.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            params.add(sb.toString());
        }
        return params.toArray(new String[0]);
    }

    /** 解析单个参数：根据值推断类型 */
    private static Object[] parseParam(String param) {
        String p = param.trim();
        // 字符串：'xxx'
        if (p.startsWith("'") && p.endsWith("'") && p.length() >= 2) {
            return new Object[]{String.class, p.substring(1, p.length() - 1)};
        }
        // 布尔
        if ("true".equalsIgnoreCase(p) || "false".equalsIgnoreCase(p)) {
            return new Object[]{Boolean.class, Boolean.valueOf(p)};
        }
        // 整数
        try {
            return new Object[]{Integer.class, Integer.valueOf(p)};
        } catch (NumberFormatException ignored) {
        }
        // 长整数
        try {
            return new Object[]{Long.class, Long.valueOf(p)};
        } catch (NumberFormatException ignored) {
        }
        // 浮点
        try {
            return new Object[]{Double.class, Double.valueOf(p)};
        } catch (NumberFormatException ignored) {
        }
        // 默认按字符串处理
        return new Object[]{String.class, p};
    }
}
