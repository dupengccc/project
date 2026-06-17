package com.mes.admin.modules.mes.cal.repository;

import com.mes.admin.modules.mes.cal.entity.CalTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CalTeamRepository extends JpaRepository<CalTeam, Long>, JpaSpecificationExecutor<CalTeam> {
}
