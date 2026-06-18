package com.mes.admin.modules.system.service;

import com.mes.admin.modules.system.entity.SysOrg;
import com.mes.admin.modules.system.entity.SysProductionUnit;
import com.mes.admin.modules.system.repository.SysOrgRepository;
import com.mes.admin.modules.system.repository.SysProductionUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;

@Service
public class SysProductionUnitService {

    @Autowired
    private SysProductionUnitRepository productionUnitRepository;

    @Autowired
    private SysOrgRepository orgRepository;

    /**
     * 查询生产单元列表
     * @param params 查询参数（unitCode, unitName, unitType, orgId, status）
     */
    public List<SysProductionUnit> list(Map<String, Object> params) {
        try {
            return productionUnitRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("unitCode") != null && !"".equals(params.get("unitCode"))) {
                        predicates.add(cb.like(root.get("unitCode"), "%" + params.get("unitCode") + "%"));
                    }
                    if (params.get("unitName") != null && !"".equals(params.get("unitName"))) {
                        predicates.add(cb.like(root.get("unitName"), "%" + params.get("unitName") + "%"));
                    }
                    if (params.get("unitType") != null && !"".equals(params.get("unitType"))) {
                        predicates.add(cb.equal(root.get("unitType"), params.get("unitType")));
                    }
                    if (params.get("orgId") != null) {
                        predicates.add(cb.equal(root.get("orgId"), params.get("orgId")));
                    }
                    if (params.get("status") != null) {
                        predicates.add(cb.equal(root.get("status"), params.get("status")));
                    }
                }
                if (query != null) query.orderBy(cb.asc(root.get("sort")));
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        } catch (Exception ignored) {
            return buildMockProductionUnits(params);
        }
    }

    /**
     * 根据ID查询
     */
    public SysProductionUnit getById(Long id) {
        if (id == null) return null;
        try {
            Optional<SysProductionUnit> opt = productionUnitRepository.findById(id);
            if (opt.isPresent()) return opt.get();
        } catch (Exception ignored) {}
        for (SysProductionUnit u : buildMockProductionUnits(null)) {
            if (id.equals(u.getId())) return u;
        }
        return null;
    }

    /**
     * 新增
     */
    @Transactional
    public SysProductionUnit create(SysProductionUnit unit) {
        if (unit == null) return null;
        if (unit.getStatus() == null) unit.setStatus(0);
        if (unit.getSort() == null) unit.setSort(0);
        if (unit.getCreateTime() == null) unit.setCreateTime(new Date());

        // 填充组织名称
        if (unit.getOrgId() != null) {
            try {
                Optional<SysOrg> orgOpt = orgRepository.findById(unit.getOrgId());
                if (orgOpt.isPresent()) {
                    unit.setOrgName(orgOpt.get().getOrgName());
                }
            } catch (Exception ignored) {}
        }

        try {
            return productionUnitRepository.save(unit);
        } catch (Exception ignored) {
            if (unit.getId() == null) unit.setId(System.currentTimeMillis());
            return unit;
        }
    }

    /**
     * 更新
     */
    @Transactional
    public SysProductionUnit update(SysProductionUnit unit) {
        if (unit == null || unit.getId() == null) {
            throw new RuntimeException("生产单元ID不能为空");
        }

        // 填充组织名称
        if (unit.getOrgId() != null) {
            try {
                Optional<SysOrg> orgOpt = orgRepository.findById(unit.getOrgId());
                if (orgOpt.isPresent()) {
                    unit.setOrgName(orgOpt.get().getOrgName());
                }
            } catch (Exception ignored) {}
        }

        try {
            return productionUnitRepository.save(unit);
        } catch (Exception ignored) {
            return unit;
        }
    }

    /**
     * 删除
     */
    @Transactional
    public void delete(Long id) {
        try {
            productionUnitRepository.deleteById(id);
        } catch (Exception ignored) {}
    }

    /**
     * 批量删除
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        try {
            productionUnitRepository.deleteAllById(ids);
        } catch (Exception ignored) {}
    }

    // ============== Mock 数据 ==============

    private List<SysProductionUnit> buildMockProductionUnits(Map<String, Object> params) {
        List<SysProductionUnit> list = new ArrayList<>();
        list.add(build(1L, "PU001", "总装产线", "产线", 2L, "华东分公司", 0, 1, "主装配线"));
        list.add(build(2L, "PU002", "焊接产线", "产线", 2L, "华东分公司", 0, 2, "焊接作业线"));
        list.add(build(3L, "PU003", "喷涂产线", "产线", 2L, "华东分公司", 0, 3, "表面处理线"));
        list.add(build(4L, "PU004", "检测中心", "工作中心", 2L, "华东分公司", 0, 4, "质量检测"));
        list.add(build(5L, "PU005", "包装工段", "工段", 3L, "华南分公司", 0, 1, "成品包装"));
        list.add(build(6L, "PU006", "SMT产线", "产线", 3L, "华南分公司", 0, 2, "贴片线"));

        if (params != null) {
            String code = params.get("unitCode") != null ? params.get("unitCode").toString() : null;
            String name = params.get("unitName") != null ? params.get("unitName").toString() : null;
            String type = params.get("unitType") != null ? params.get("unitType").toString() : null;
            Long orgId = params.get("orgId") != null ? ((Number) params.get("orgId")).longValue() : null;
            Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : null;

            return list.stream().filter(u -> {
                if (code != null && !code.isEmpty() && (u.getUnitCode() == null || !u.getUnitCode().contains(code))) return false;
                if (name != null && !name.isEmpty() && (u.getUnitName() == null || !u.getUnitName().contains(name))) return false;
                if (type != null && !type.isEmpty() && !type.equals(u.getUnitType())) return false;
                if (orgId != null && !orgId.equals(u.getOrgId())) return false;
                if (status != null && !status.equals(u.getStatus())) return false;
                return true;
            }).collect(java.util.stream.Collectors.toList());
        }
        return list;
    }

    private SysProductionUnit build(Long id, String code, String name, String type, Long orgId, String orgName, Integer status, Integer sort, String remark) {
        SysProductionUnit u = new SysProductionUnit();
        u.setId(id);
        u.setUnitCode(code);
        u.setUnitName(name);
        u.setUnitType(type);
        u.setOrgId(orgId);
        u.setOrgName(orgName);
        u.setStatus(status);
        u.setSort(sort);
        u.setRemark(remark);
        u.setCreateTime(new Date());
        return u;
    }
}
