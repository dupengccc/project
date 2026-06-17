package com.mes.admin.modules.mes.md.repository;

import com.mes.admin.modules.mes.md.entity.MdCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MdCustomerRepository extends JpaRepository<MdCustomer, Long>, JpaSpecificationExecutor<MdCustomer> {

    Optional<MdCustomer> findByCustomerCode(String customerCode);
}
