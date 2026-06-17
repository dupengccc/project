package com.mes.admin.modules.mes.md.repository;

import com.mes.admin.modules.mes.md.entity.MdMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MdMaterialRepository extends JpaRepository<MdMaterial, Long>, JpaSpecificationExecutor<MdMaterial> {

    Optional<MdMaterial> findByMaterialCode(String materialCode);
}
