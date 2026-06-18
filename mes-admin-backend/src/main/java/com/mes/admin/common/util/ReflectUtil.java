package com.mes.admin.common.util;

import java.lang.reflect.*;
import java.util.*;

/**
 * 反射工具类
 * <p>
 * 功能包括：
 * - 对象属性读取/设置
 * - 属性类型转换
 * - Bean 复制
 * - 类信息获取
 * </p>
 *
 * @author MES Admin
 */
public class ReflectUtil {

    // ==================== 获取字段值 ====================

    /**
     * 获取字段值
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null || StringUtil.isEmpty(fieldName)) {
            return null;
        }
        try {
            Field field = getDeclaredField(obj.getClass(), fieldName);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 获取字段值（支持链式访问，如 user.address.city）
     */
    public static Object getFieldValueChain(Object obj, String fieldPath) {
        if (obj == null || StringUtil.isEmpty(fieldPath)) {
            return null;
        }
        String[] parts = fieldPath.split("\\.");
        Object current = obj;
        for (String part : parts) {
            if (current == null) {
                return null;
            }
            current = getFieldValue(current, part);
        }
        return current;
    }

    /**
     * 设置字段值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        if (obj == null || StringUtil.isEmpty(fieldName)) {
            return;
        }
        try {
            Field field = getDeclaredField(obj.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(obj, convertValue(value, field.getType()));
            }
        } catch (IllegalAccessException e) {
            // 忽略
        }
    }

    // ==================== 获取字段 ====================

    /**
     * 获取声明的字段（包括父类）
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        if (clazz == null || StringUtil.isEmpty(fieldName)) {
            return null;
        }
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 获取所有声明的字段（包括父类）
     */
    public static List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    // ==================== 获取方法 ====================

    /**
     * 获取声明的方法（包括父类）
     */
    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        if (clazz == null || StringUtil.isEmpty(methodName)) {
            return null;
        }
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 获取所有声明的方法（包括父类）
     */
    public static List<Method> getDeclaredMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    /**
     * 调用方法
     */
    public static Object invokeMethod(Object obj, String methodName, Object... args) {
        if (obj == null || StringUtil.isEmpty(methodName)) {
            return null;
        }
        try {
            Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i] == null ? Object.class : args[i].getClass();
            }
            Method method = getDeclaredMethod(obj.getClass(), methodName, paramTypes);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(obj, args);
            }
        } catch (Exception e) {
            // 忽略
        }
        return null;
    }

    // ==================== Bean 复制 ====================

    /**
     * 对象属性复制
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null || targetClass == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象复制失败", e);
        }
    }

    /**
     * 对象属性复制
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        List<Field> sourceFields = getDeclaredFields(source.getClass());
        List<Field> targetFields = getDeclaredFields(target.getClass());

        Map<String, Field> targetFieldMap = new HashMap<>();
        for (Field field : targetFields) {
            targetFieldMap.put(field.getName(), field);
        }

        for (Field sourceField : sourceFields) {
            Field targetField = targetFieldMap.get(sourceField.getName());
            if (targetField != null && isSimpleType(sourceField.getType())) {
                try {
                    sourceField.setAccessible(true);
                    targetField.setAccessible(true);
                    Object value = sourceField.get(source);
                    targetField.set(target, convertValue(value, targetField.getType()));
                } catch (IllegalAccessException e) {
                    // 忽略
                }
            }
        }
    }

    /**
     * Map 转对象
     */
    public static <T> T mapToObject(Map<String, ?> map, Class<T> clazz) {
        if (map == null || clazz == null) {
            return null;
        }
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                setFieldValue(obj, entry.getKey(), entry.getValue());
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Map 转对象失败", e);
        }
    }

    /**
     * 对象转 Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        List<Field> fields = getDeclaredFields(obj.getClass());
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value != null) {
                    map.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                // 忽略
            }
        }
        return map;
    }

    // ==================== 类型判断 ====================

    /**
     * 判断是否为简单类型
     */
    public static boolean isSimpleType(Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() ||
               type == String.class ||
               type == Integer.class ||
               type == Long.class ||
               type == Double.class ||
               type == Float.class ||
               type == Boolean.class ||
               type == Character.class ||
               type == Byte.class ||
               type == Short.class ||
               Number.class.isAssignableFrom(type) ||
               CharSequence.class.isAssignableFrom(type) ||
               Date.class.isAssignableFrom(type);
    }

    /**
     * 判断是否为包装类型
     */
    public static boolean isWrapperType(Class<?> type) {
        return type == Integer.class ||
               type == Long.class ||
               type == Double.class ||
               type == Float.class ||
               type == Boolean.class ||
               type == Character.class ||
               type == Byte.class ||
               type == Short.class;
    }

    /**
     * 获取包装类型对应的原始类型
     */
    public static Class<?> getPrimitiveType(Class<?> wrapperType) {
        if (wrapperType == Integer.class) return int.class;
        if (wrapperType == Long.class) return long.class;
        if (wrapperType == Double.class) return double.class;
        if (wrapperType == Float.class) return float.class;
        if (wrapperType == Boolean.class) return boolean.class;
        if (wrapperType == Character.class) return char.class;
        if (wrapperType == Byte.class) return byte.class;
        if (wrapperType == Short.class) return short.class;
        return wrapperType;
    }

    // ==================== 类型转换 ====================

    /**
     * 值类型转换
     */
    public static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType.isInstance(value)) {
            return value;
        }

        String strValue = value.toString();

        if (targetType == String.class) {
            return strValue;
        }
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(strValue);
        }
        if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(strValue);
        }
        if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(strValue);
        }
        if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(strValue);
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(strValue);
        }
        if (targetType == short.class || targetType == Short.class) {
            return Short.parseShort(strValue);
        }
        if (targetType == byte.class || targetType == Byte.class) {
            return Byte.parseByte(strValue);
        }
        if (targetType == char.class || targetType == Character.class) {
            return strValue.charAt(0);
        }

        return value;
    }

    // ==================== 类信息 ====================

    /**
     * 获取类名（不含包名）
     */
    public static String getSimpleClassName(Class<?> clazz) {
        if (clazz == null) return "";
        return clazz.getSimpleName();
    }

    /**
     * 获取类名（不含包名）
     */
    public static String getSimpleClassName(Object obj) {
        return obj == null ? "" : getSimpleClassName(obj.getClass());
    }

    /**
     * 判断类是否存在
     */
    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取泛型类型
     */
    public static Class<?> getGenericType(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] typeArgs = paramType.getActualTypeArguments();
            if (typeArgs != null && typeArgs.length > 0) {
                return (Class<?>) typeArgs[0];
            }
        }
        return null;
    }

    // ==================== 集合操作 ====================

    /**
     * 获取集合元素类型
     */
    public static Class<?> getCollectionElementType(Field field) {
        if (field == null) {
            return null;
        }
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] typeArgs = paramType.getActualTypeArguments();
            if (typeArgs != null && typeArgs.length > 0) {
                return (Class<?>) typeArgs[0];
            }
        }
        return Object.class;
    }

    // ==================== 对象比较 ====================

    /**
     * 比较两个对象的所有属性值是否相等
     */
    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        if (obj1.getClass() != obj2.getClass()) {
            return false;
        }
        List<Field> fields = getDeclaredFields(obj1.getClass());
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value1 = field.get(obj1);
                Object value2 = field.get(obj2);
                if (!Objects.equals(value1, value2)) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成对象的哈希码
     */
    public static int hashCode(Object obj) {
        if (obj == null) {
            return 0;
        }
        List<Field> fields = getDeclaredFields(obj.getClass());
        int hashCode = 17;
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                hashCode = 31 * hashCode + (value == null ? 0 : value.hashCode());
            } catch (IllegalAccessException e) {
                // 忽略
            }
        }
        return hashCode;
    }
}
