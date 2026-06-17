package com.mes.admin.modules.mes.md.repository;

import com.mes.admin.modules.mes.md.entity.MdProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MdProcessRepository extends JpaRepository<MdProcess, Long>, JpaSpecificationExecutor<MdProcess> {

    Optional<MdProcess> findByProcessCode(String processCode);
}
