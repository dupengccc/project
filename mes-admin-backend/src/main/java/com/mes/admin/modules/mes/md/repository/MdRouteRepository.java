package com.mes.admin.modules.mes.md.repository;

import com.mes.admin.modules.mes.md.entity.MdRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MdRouteRepository extends JpaRepository<MdRoute, Long>, JpaSpecificationExecutor<MdRoute> {

    Optional<MdRoute> findByRouteCode(String routeCode);
}
