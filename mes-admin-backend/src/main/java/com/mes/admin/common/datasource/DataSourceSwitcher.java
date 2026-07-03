package com.mes.admin.common.datasource;

/**
 * 数据源切换工具类
 *
 * 支持手动切换和自动恢复两种模式
 *
 * 使用示例：
 *
 * 1. 手动切换（需要手动恢复）
 *    DataSourceSwitcher.switchTo("slave");
 *    try {
 *        // 使用从数据源的查询
 *        List<MdMaterial> list = sqlQuery.findList(MdMaterial.class, "SELECT * FROM mes_md_material");
 *    } finally {
 *        DataSourceSwitcher.restore(); // 恢复默认数据源
 *    }
 *
 * 2. 自动切换（使用 Lambda，推荐）
 *    List<MdMaterial> list = DataSourceSwitcher.execute("slave", () -> {
 *        return sqlQuery.findList(MdMaterial.class, "SELECT * FROM mes_md_material");
 *    });
 *
 * 3. 使用 @DataSource 注解（推荐，AOP 自动处理）
 *    @DataSource("slave")
 *    public List<MdMaterial> findAllMaterials() {
 *        return sqlQuery.findList(MdMaterial.class, "SELECT * FROM mes_md_material");
 *    }
 */
public class DataSourceSwitcher {

    public static final String MASTER = "master";
    public static final String SLAVE = "slave";

    /**
     * 切换到指定数据源
     *
     * @param dataSource 数据源名称（master/slave）
     */
    public static void switchTo(String dataSource) {
        DynamicDataSourceContextHolder.setDataSourceType(dataSource);
    }

    /**
     * 恢复到默认数据源
     */
    public static void restore() {
        DynamicDataSourceContextHolder.clearDataSourceType();
    }

    /**
     * 执行并自动切换数据源（Lambda 方式）
     *
     * @param dataSource 数据源名称
     * @param action 要执行的操作
     * @param <T> 返回值类型
     * @return 执行结果
     */
    public static <T> T execute(String dataSource, DataSourceAction<T> action) {
        String original = DynamicDataSourceContextHolder.getDataSourceType();
        try {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource);
            return action.execute();
        } finally {
            if (original != null) {
                DynamicDataSourceContextHolder.setDataSourceType(original);
            } else {
                DynamicDataSourceContextHolder.clearDataSourceType();
            }
        }
    }

    /**
     * 在主数据源执行
     */
    public static <T> T executeOnMaster(DataSourceAction<T> action) {
        return execute(MASTER, action);
    }

    /**
     * 在从数据源执行
     */
    public static <T> T executeOnSlave(DataSourceAction<T> action) {
        return execute(SLAVE, action);
    }

    /**
     * 获取当前数据源
     */
    public static String current() {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }

    /**
     * 是否在指定数据源上执行
     */
    public static boolean isCurrent(String dataSource) {
        return dataSource != null && dataSource.equals(current());
    }

    /**
     * Lambda 表达式接口
     */
    @FunctionalInterface
    public interface DataSourceAction<T> {
        T execute();
    }
}
