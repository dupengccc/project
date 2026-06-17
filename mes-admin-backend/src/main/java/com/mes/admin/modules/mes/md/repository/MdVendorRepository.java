package com.mes.admin.modules.mes.md.repository;

import com.mes.admin.modules.mes.md.entity.MdVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MdVendorRepository extends JpaRepository<MdVendor, Long>, JpaSpecificationExecutor<MdVendor> {

    Optional<MdVendor> findByVendorCode(String vendorCode);
}
