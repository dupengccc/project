package com.mes.admin.common.util;

import com.mes.admin.common.datasource.DataSource;
import com.mes.admin.common.datasource.DataSourceSwitcher;
import com.mes.admin.modules.system.entity.SysUser;
import com.mes.admin.modules.mes.md.entity.MdMaterial;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlQueryTemplate 使用示例
 *
 * 展示各种查询场景的使用方法
 */
@Component
public class SqlQueryExamples {

    @Resource
    private SqlQueryTemplate sqlQuery;

    // ==================== 基础查询 ====================

    /**
     * 示例1：查询单个对象
     */
    @DataSource("master")
    public SysUser findUserById(Long id) {
        return sqlQuery.findOne(SysUser.class,
                "SELECT id, username, nickname, email, phone, status FROM sys_user WHERE id = ?", id);
    }

    /**
     * 示例2：查询列表
     */
    @DataSource("master")
    public List<SysUser> findActiveUsers() {
        return sqlQuery.findList(SysUser.class,
                "SELECT id, username, nickname, email, status FROM sys_user WHERE status = 0");
    }

    /**
     * 示例3：带参数查询
     */
    @DataSource("master")
    public List<SysUser> findUsersByStatus(Integer status) {
        return sqlQuery.findList(SysUser.class,
                "SELECT * FROM sys_user WHERE status = ?", status);
    }

    // ==================== 跨数据源查询 ====================

    /**
     * 示例4：查询 MES 数据源（使用 @DataSource 注解）
     */
    @DataSource("mes")
    public List<MdMaterial> findMaterialsByType(String materialType) {
        return sqlQuery.findList(MdMaterial.class,
                "SELECT * FROM mes_md_material WHERE material_type = ?", materialType);
    }

    /**
     * 示例5：使用 DataSourceSwitcher 手动切换
     */
    public List<MdMaterial> findMaterialsManual() {
        return DataSourceSwitcher.executeOnMes(() ->
                sqlQuery.findList(MdMaterial.class,
                        "SELECT * FROM mes_md_material WHERE status = 0")
        );
    }

    // ==================== 命名参数查询 ====================

    /**
     * 示例6：命名参数查询
     */
    @DataSource("master")
    public List<SysUser> findUsersByNamedParams(String username, Integer status) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "%" + username + "%");
        params.put("status", status);
        return sqlQuery.findListByNamedParams(SysUser.class,
                "SELECT * FROM sys_user WHERE username LIKE :username AND status = :status", params);
    }

    // ==================== 聚合查询 ====================

    /**
     * 示例7：查询单值（COUNT）
     */
    @DataSource("master")
    public Long countActiveUsers() {
        return sqlQuery.queryForLong("SELECT COUNT(*) FROM sys_user WHERE status = 0");
    }

    /**
     * 示例8：查询单值（求和）
     */
    @DataSource("mes")
    public Double sumMaterialSafeStock() {
        return sqlQuery.queryForValue("SELECT SUM(safe_stock) FROM mes_md_material WHERE status = 0");
    }

    // ==================== 动态条件查询 ====================

    /**
     * 示例9：动态 SQL 查询
     */
    @DataSource("master")
    public List<SysUser> searchUsers(String username, Integer status, String email) {
        return sqlQuery.findByConditions(
                SysUser.class,
                "SELECT * FROM sys_user",
                SqlQueryTemplate.Condition.like("username", username),
                SqlQueryTemplate.Condition.eq("status", status),
                SqlQueryTemplate.Condition.like("email", email)
        );
    }

    // ==================== 分页查询 ====================

    /**
     * 示例10：分页查询
     */
    @DataSource("mes")
    public Map<String, Object> findMaterialsPage(int page, int pageSize, String materialType) {
        int offset = (page - 1) * pageSize;

        String countSql = "SELECT COUNT(*) FROM mes_md_material WHERE material_type = ?";
        String dataSql = "SELECT * FROM mes_md_material WHERE material_type = ? ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        // 注意：达梦/Oracle 使用 OFFSET ... ROWS FETCH NEXT ... ROWS ONLY
        // MySQL 使用 LIMIT ... OFFSET ...
        return sqlQuery.findPage(MdMaterial.class, countSql, dataSql, materialType, offset, pageSize);
    }

    // ==================== Map 查询 ====================

    /**
     * 示例11：返回 Map 格式
     */
    @DataSource("master")
    public Map<String, Object> findUserAsMap(Long id) {
        return sqlQuery.findMap("SELECT * FROM sys_user WHERE id = ?", id);
    }

    /**
     * 示例12：返回 Map 列表
     */
    @DataSource("master")
    public List<Map<String, Object>> findAllUsersAsMap() {
        return sqlQuery.findMapList("SELECT id, username, nickname, status FROM sys_user");
    }

    // ==================== 增删改操作 ====================

    /**
     * 示例13：执行更新
     */
    @DataSource("master")
    public int updateUserStatus(Long id, Integer status) {
        return sqlQuery.executeUpdate("UPDATE sys_user SET status = ? WHERE id = ?", status, id);
    }

    /**
     * 示例14：批量更新
     */
    @DataSource("master")
    public int[] batchUpdateUserStatus(List<Long> ids, Integer status) {
        List<Object[]> batchValues = new java.util.ArrayList<>();
        for (Long id : ids) {
            batchValues.add(new Object[]{status, id});
        }
        return sqlQuery.executeBatch("UPDATE sys_user SET status = ? WHERE id = ?", batchValues);
    }

    // ==================== 复杂查询示例 ====================

    /**
     * 示例15：多表关联查询（返回实体需确保列名与属性名对应）
     */
    @DataSource("master")
    public List<Map<String, Object>> findUserRoleInfo() {
        String sql = "SELECT u.id, u.username, u.nickname, r.role_name " +
                "FROM sys_user u " +
                "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
                "LEFT JOIN sys_role r ON ur.role_id = r.id " +
                "WHERE u.status = 0";
        return sqlQuery.findMapList(sql);
    }
}
