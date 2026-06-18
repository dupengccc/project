package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysProductionUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SysProductionUnitRepository extends JpaRepository<SysProductionUnit, Long>, JpaSpecificationExecutor<SysProductionUnit> {

    /** 按组织ID查询 */
    List<SysProductionUnit> findByOrgId(Long orgId);

    /** 按编码查询 */
    SysProductionUnit findByUnitCode(String unitCode);

    /** 检查编码是否存在 */
    boolean existsByUnitCode(String unitCode);
}
