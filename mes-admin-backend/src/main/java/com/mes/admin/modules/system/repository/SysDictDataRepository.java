package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysDictData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SysDictDataRepository extends JpaRepository<SysDictData, Long>, JpaSpecificationExecutor<SysDictData> {

    List<SysDictData> findByDictType(String dictType);
}
