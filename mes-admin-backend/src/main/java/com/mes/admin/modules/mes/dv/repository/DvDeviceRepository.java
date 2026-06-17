package com.mes.admin.modules.mes.dv.repository;

import com.mes.admin.modules.mes.dv.entity.DvDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DvDeviceRepository extends JpaRepository<DvDevice, Long>, JpaSpecificationExecutor<DvDevice> {
}
