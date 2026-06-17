package com.mes.admin.modules.mes.md.repository;

import com.mes.admin.modules.mes.md.entity.MdWorkstation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MdWorkstationRepository extends JpaRepository<MdWorkstation, Long>, JpaSpecificationExecutor<MdWorkstation> {

    Optional<MdWorkstation> findByStationCode(String stationCode);
}
