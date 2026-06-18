package com.mes.admin.modules.system.repository;

import com.mes.admin.modules.system.entity.SysDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 数据字典 Repository（父子层级）
 */
public interface SysDictRepository extends JpaRepository<SysDict, Long>,
        JpaSpecificationExecutor<SysDict> {

    /** 查询根节点（parentId = 0） */
    List<SysDict> findByParentIdOrderBySort(Long parentId);

    /** 按父编码查询子节点 */
    List<SysDict> findByParentCodeOrderBySort(String parentCode);

    /** 查询同父节点下的所有字典（排除自己） */
    List<SysDict> findByParentIdAndIdNot(Long parentId, Long id);

    /** 按字典编码查询（唯一） */
    List<SysDict> findByDictCode(String dictCode);
}
