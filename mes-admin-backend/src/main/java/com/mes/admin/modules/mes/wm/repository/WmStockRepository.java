package com.mes.admin.modules.mes.wm.repository;

import com.mes.admin.modules.mes.wm.entity.WmStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WmStockRepository extends JpaRepository<WmStock, Long>, JpaSpecificationExecutor<WmStock> {
}
