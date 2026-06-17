package com.mes.admin.modules.mes.md.repository;

import com.mes.admin.modules.mes.md.entity.MdWorkshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MdWorkshopRepository extends JpaRepository<MdWorkshop, Long>, JpaSpecificationExecutor<MdWorkshop> {

    Optional<MdWorkshop> findByWorkshopCode(String workshopCode);
}
