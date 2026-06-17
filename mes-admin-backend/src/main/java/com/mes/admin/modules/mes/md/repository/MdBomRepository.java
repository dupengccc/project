package com.mes.admin.modules.mes.md.repository;

import com.mes.admin.modules.mes.md.entity.MdBom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MdBomRepository extends JpaRepository<MdBom, Long>, JpaSpecificationExecutor<MdBom> {

    Optional<MdBom> findByBomCode(String bomCode);
}
