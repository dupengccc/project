package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysInterfaceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SysInterfaceConfigRepository extends JpaRepository<SysInterfaceConfig, Long>, JpaSpecificationExecutor<SysInterfaceConfig> {

    /** 根据接口编码查询 */
    Optional<SysInterfaceConfig> findByInterfaceCode(String interfaceCode);

    /** 根据接口编码删除 */
    void deleteByInterfaceCode(String interfaceCode);

    /** 查询启用的接口列表 */
    List<SysInterfaceConfig> findByStatus(Integer status);

    /** 根据所属系统查询 */
    List<SysInterfaceConfig> findByOwnerSystem(String ownerSystem);
}
