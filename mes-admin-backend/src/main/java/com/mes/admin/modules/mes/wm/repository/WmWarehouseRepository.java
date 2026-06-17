package com.mes.admin.modules.mes.wm.repository;

import com.mes.admin.modules.mes.wm.entity.WmWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WmWarehouseRepository extends JpaRepository<WmWarehouse, Long>, JpaSpecificationExecutor<WmWarehouse> {
}
