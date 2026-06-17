package com.mes.admin.modules.mes.pro.repository;

import com.mes.admin.modules.mes.pro.entity.ProWorkorder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProWorkorderRepository extends JpaRepository<ProWorkorder, Long>, JpaSpecificationExecutor<ProWorkorder> {
}
