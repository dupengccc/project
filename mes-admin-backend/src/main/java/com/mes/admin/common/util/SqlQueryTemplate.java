package com.mes.admin.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 通用 SQL 查询模板
 * 支持：
 * 1. 原生 SQL 查询（无需通过 Repository）
 * 2. 结果自动映射到实体对象
 * 3. 支持多数据源切换（配合 @DataSource 注解使用）
 * 4. 支持命名参数查询
 *
 * 使用示例：
 *
 * 1. 查询单个对象
 *    SysUser user = sqlQuery.findOne(SysUser.class, "SELECT * FROM sys_user WHERE id = ?", 1L);
 *
 * 2. 查询列表
 *    List<SysUser> users = sqlQuery.findList(SysUser.class, "SELECT * FROM sys_user WHERE status = ?", 0);
 *
 * 3. 命名参数查询
 *    Map<String, Object> params = new HashMap<>();
 *    params.put("id", 1L);
 *    params.put("status", 0);
 *    List<SysUser> users = sqlQuery.findByNamedParams(SysUser.class, "SELECT * FROM sys_user WHERE id=:id AND status=:status", params);
 *
 * 4. 查询单值（聚合函数等）
 *    Long count = sqlQuery.queryForValue("SELECT COUNT(*) FROM sys_user WHERE status = ?", 0);
 *
 * 5. 执行更新/删除
 *    int rows = sqlQuery.executeUpdate("UPDATE sys_user SET status = ? WHERE id = ?", 1, 1L);
 */
@Component
public class SqlQueryTemplate {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // ==================== 查询单个对象 ====================

    /**
     * 查询单个对象（自动映射到实体）
     *
     * @param entityClass 实体类 Class
     * @param sql SQL 语句（占位符用 ?）
     * @param args 参数
     * @param <T> 实体类型
     * @return 实体对象，未找到返回 null
     */
    public <T> T findOne(Class<T> entityClass, String sql, Object... args) {
        List<T> list = findList(entityClass, sql, args);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 查询单个对象（带 RowMapper）
     *
     * @param rowMapper 行映射器
     * @param sql SQL 语句
     * @param args 参数
     * @param <T> 结果类型
     * @return 对象，未找到返回 null
     */
    public <T> T findOne(RowMapper<T> rowMapper, String sql, Object... args) {
        List<T> list = jdbcTemplate.query(sql, rowMapper, args);
        return list.isEmpty() ? null : list.get(0);
    }

    // ==================== 查询列表 ====================

    /**
     * 查询列表（自动映射到实体）
     *
     * @param entityClass 实体类 Class
     * @param sql SQL 语句
     * @param args 参数
     * @param <T> 实体类型
     * @return 实体列表
     */
    public <T> List<T> findList(Class<T> entityClass, String sql, Object... args) {
        return jdbcTemplate.query(sql, new EntityRowMapper<>(entityClass), args);
    }

    /**
     * 查询列表（带 RowMapper）
     *
     * @param rowMapper 行映射器
     * @param sql SQL 语句
     * @param args 参数
     * @param <T> 结果类型
     * @return 对象列表
     */
    public <T> List<T> findList(RowMapper<T> rowMapper, String sql, Object... args) {
        return jdbcTemplate.query(sql, rowMapper, args);
    }

    // ==================== 命名参数查询 ====================

    /**
     * 命名参数查询单个对象
     *
     * @param entityClass 实体类 Class
     * @param sql SQL 语句（占位符用 :paramName）
     * @param params 参数 Map
     * @param <T> 实体类型
     * @return 实体对象
     */
    public <T> T findOneByNamedParams(Class<T> entityClass, String sql, Map<String, ?> params) {
        List<T> list = findListByNamedParams(entityClass, sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 命名参数查询列表
     *
     * @param entityClass 实体类 Class
     * @param sql SQL 语句（占位符用 :paramName）
     * @param params 参数 Map
     * @param <T> 实体类型
     * @return 实体列表
     */
    public <T> List<T> findListByNamedParams(Class<T> entityClass, String sql, Map<String, ?> params) {
        return namedParameterJdbcTemplate.query(sql, params, new EntityRowMapper<>(entityClass));
    }

    // ==================== Map 查询 ====================

    /**
     * 查询单行，返回 Map
     *
     * @param sql SQL 语句
     * @param args 参数
     * @return 列名-值 Map
     */
    public Map<String, Object> findMap(String sql, Object... args) {
        List<Map<String, Object>> list = findMapList(sql, args);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 查询多行，返回 Map 列表
     *
     * @param sql SQL 语句
     * @param args 参数
     * @return Map 列表
     */
    public List<Map<String, Object>> findMapList(String sql, Object... args) {
        return jdbcTemplate.queryForList(sql, args);
    }

    // ==================== 单值查询 ====================

    /**
     * 查询单值（用于 COUNT、SUM、MAX 等聚合函数）
     *
     * @param sql SQL 语句
     * @param args 参数
     * @param <T> 值类型
     * @return 单值
     */
    @SuppressWarnings("unchecked")
    public <T> T queryForValue(String sql, Object... args) {
        Number value = jdbcTemplate.queryForObject(sql, Number.class, args);
        return value != null ? (T) value : null;
    }

    /**
     * 查询整型值
     */
    public Integer queryForInt(String sql, Object... args) {
        Integer value = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return value != null ? value : 0;
    }

    /**
     * 查询长整型值
     */
    public Long queryForLong(String sql, Object... args) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class, args);
        return value != null ? value : 0L;
    }

    /**
     * 查询字符串值
     */
    public String queryForString(String sql, Object... args) {
        String value = jdbcTemplate.queryForObject(sql, String.class, args);
        return value != null ? value : "";
    }

    // ==================== 更新操作 ====================

    /**
     * 执行更新（INSERT、UPDATE、DELETE）
     *
     * @param sql SQL 语句
     * @param args 参数
     * @return 影响行数
     */
    public int executeUpdate(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    /**
     * 批量更新
     *
     * @param sql SQL 语句
     * @param batchValues 批量参数
     * @return 每条的影响行数数组
     */
    public int[] executeBatch(String sql, List<Object[]> batchValues) {
        return jdbcTemplate.batchUpdate(sql, batchValues);
    }

    /**
     * 命名参数更新
     *
     * @param sql SQL 语句
     * @param params 参数 Map
     * @return 影响行数
     */
    public int executeUpdateByNamedParams(String sql, Map<String, ?> params) {
        return namedParameterJdbcTemplate.update(sql, params);
    }

    // ==================== 分页查询 ====================

    /**
     * 分页查询
     *
     * @param entityClass 实体类
     * @param countSql 统计总数 SQL
     * @param dataSql 查询数据 SQL
     * @param args 参数
     * @param <T> 实体类型
     * @return 包含 total 和 list 的 Map
     */
    public <T> Map<String, Object> findPage(Class<T> entityClass, String countSql, String dataSql, Object... args) {
        Map<String, Object> result = new HashMap<>();

        // 查询总数
        Long total = queryForLong(countSql, args);
        result.put("total", total);

        // 查询分页数据
        List<T> list = findList(entityClass, dataSql, args);
        result.put("list", list);

        return result;
    }

    // ==================== 动态 SQL 构建 ====================

    /**
     * 动态 SQL 查询
     * 只拼接非空条件
     *
     * @param entityClass 实体类
     * @param baseSql 基础 SQL（不含 WHERE）
     * @param conditions 动态条件
     * @return 实体列表
     */
    public <T> List<T> findByConditions(Class<T> entityClass, String baseSql, Condition... conditions) {
        StringBuilder sql = new StringBuilder(baseSql);
        List<Object> args = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1");

        for (Condition condition : conditions) {
            if (condition.isValid()) {
                where.append(" AND ").append(condition.getColumn()).append(" ").append(condition.getOperator()).append(" ?");
                args.add(condition.getValue());
            }
        }

        sql.append(where);
        return findList(entityClass, sql.toString(), args.toArray());
    }

    // ==================== 实体映射器 ====================

    /**
     * 实体 RowMapper
     * 根据 ResultSetMetaData 自动映射列名到实体属性
     */
    public static class EntityRowMapper<T> implements RowMapper<T> {

        private final Class<T> entityClass;
        private final Map<String, String> columnFieldMap; // 列名 -> 字段名

        public EntityRowMapper(Class<T> entityClass) {
            this.entityClass = entityClass;
            this.columnFieldMap = buildColumnFieldMap();
        }

        private Map<String, String> buildColumnFieldMap() {
            Map<String, String> map = new HashMap<>();
            // 实体所有字段（包含父类字段）
            java.lang.reflect.Field[] fields = getAllFields(entityClass);
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                // 列名：下划线转驼峰
                String columnName = underscoreToCamel(field.getName());
                map.put(columnName.toUpperCase(), field.getName());
            }
            return map;
        }

        private java.lang.reflect.Field[] getAllFields(Class<?> clazz) {
            java.util.List<java.lang.reflect.Field> fieldList = new ArrayList<>();
            while (clazz != null && clazz != Object.class) {
                java.util.Collections.addAll(fieldList, clazz.getDeclaredFields());
                clazz = clazz.getSuperclass();
            }
            return fieldList.toArray(new java.lang.reflect.Field[0]);
        }

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                T entity = entityClass.getDeclaredConstructor().newInstance();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i); // 使用 AS 别名
                    String columnName = metaData.getColumnName(i);   // 原始列名

                    // 优先用 AS 别名匹配字段
                    String fieldName = columnFieldMap.get(columnLabel.toUpperCase());
                    if (fieldName == null) {
                        fieldName = columnFieldMap.get(columnName.toUpperCase());
                    }
                    if (fieldName == null) {
                        // 尝试下划线转驼峰匹配
                        fieldName = columnFieldMap.get(underscoreToCamel(columnLabel).toUpperCase());
                    }
                    if (fieldName == null) {
                        continue;
                    }

                    java.lang.reflect.Field field = null;
                    try {
                        field = entityClass.getDeclaredField(fieldName);
                    } catch (NoSuchFieldException e) {
                        // 尝试从父类找
                        for (java.lang.reflect.Field f : getAllFields(entityClass)) {
                            if (f.getName().equals(fieldName)) {
                                field = f;
                                break;
                            }
                        }
                    }
                    if (field == null) continue;

                    field.setAccessible(true);
                    Object value = getResultSetValue(rs, i, field.getType());
                    field.set(entity, value);
                }
                return entity;
            } catch (Exception e) {
                throw new SQLException("映射实体失败: " + entityClass.getName(), e);
            }
        }

        private Object getResultSetValue(ResultSet rs, int columnIndex, Class<?> targetType) throws SQLException {
            Object value = rs.getObject(columnIndex);
            if (value == null) return null;

            // 类型转换
            if (targetType == Long.class || targetType == long.class) {
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
                return Long.parseLong(value.toString());
            }
            if (targetType == Integer.class || targetType == int.class) {
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                return Integer.parseInt(value.toString());
            }
            if (targetType == Double.class || targetType == double.class) {
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                return Double.parseDouble(value.toString());
            }
            if (targetType == Float.class || targetType == float.class) {
                if (value instanceof Number) {
                    return ((Number) value).floatValue();
                }
                return Float.parseFloat(value.toString());
            }
            if (targetType == Boolean.class || targetType == boolean.class) {
                if (value instanceof Boolean) {
                    return value;
                }
                return "1".equals(value.toString()) || "true".equalsIgnoreCase(value.toString());
            }
            if (targetType == java.util.Date.class) {
                if (value instanceof java.sql.Timestamp) {
                    return new java.util.Date(((java.sql.Timestamp) value).getTime());
                }
                if (value instanceof java.sql.Date) {
                    return new java.util.Date(((java.sql.Date) value).getTime());
                }
                return value;
            }
            return value;
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 下划线转驼峰
     * user_id -> userId
     * create_time -> createTime
     */
    public static String underscoreToCamel(String str) {
        if (str == null || str.isEmpty()) return str;
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                upperNext = true;
            } else {
                if (upperNext) {
                    sb.append(Character.toUpperCase(c));
                    upperNext = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     * userId -> user_id
     */
    public static String camelToUnderscore(String str) {
        if (str == null || str.isEmpty()) return str;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) sb.append('_');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ==================== 动态条件类 ====================

    /**
     * 动态查询条件
     */
    public static class Condition {
        private final String column;
        private final String operator;
        private final Object value;

        public Condition(String column, Object value) {
            this(column, "=", value);
        }

        public Condition(String column, String operator, Object value) {
            this.column = column;
            this.operator = operator;
            this.value = value;
        }

        public boolean isValid() {
            return value != null && !"".equals(value);
        }

        public String getColumn() { return column; }
        public String getOperator() { return operator; }
        public Object getValue() { return value; }

        // 常用条件工厂方法
        public static Condition eq(String column, Object value) {
            return new Condition(column, value);
        }
        public static Condition ne(String column, Object value) {
            return new Condition(column, "<>", value);
        }
        public static Condition like(String column, String value) {
            return new Condition(column, "LIKE", "%" + value + "%");
        }
        public static Condition gt(String column, Object value) {
            return new Condition(column, ">", value);
        }
        public static Condition ge(String column, Object value) {
            return new Condition(column, ">=", value);
        }
        public static Condition lt(String column, Object value) {
            return new Condition(column, "<", value);
        }
        public static Condition le(String column, Object value) {
            return new Condition(column, "<=", value);
        }
        public static Condition in(String column, Object value) {
            return new Condition(column, "IN", value);
        }
        public static Condition isNull(String column) {
            return new Condition(column, "IS NULL", null);
        }
        public static Condition isNotNull(String column) {
            return new Condition(column, "IS NOT NULL", null);
        }
    }
}
