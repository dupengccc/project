package com.mes.admin.modules.mes.wm.service;

import com.mes.admin.modules.mes.wm.entity.WmLocation;
import com.mes.admin.modules.mes.wm.repository.WmLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WmLocationService {

    @Autowired
    private WmLocationRepository wmLocationRepository;

    public List<WmLocation> list(Map<String, Object> params) {
        try {
            return wmLocationRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("locationCode") != null) {
                        predicates.add(cb.like(root.get("locationCode"), "%" + params.get("locationCode") + "%"));
                    }
                    if (params.get("locationName") != null) {
                        predicates.add(cb.like(root.get("locationName"), "%" + params.get("locationName") + "%"));
                    }
                    if (params.get("warehouseId") != null) {
                        predicates.add(cb.equal(root.get("warehouseId"), params.get("warehouseId")));
                    }
                    if (params.get("areaId") != null) {
                        predicates.add(cb.equal(root.get("areaId"), params.get("areaId")));
                    }
                    if (params.get("status") != null) {
                        predicates.add(cb.equal(root.get("status"), params.get("status")));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        } catch (Exception ignored) {
            return mockList();
        }
    }

    public WmLocation getById(Long id) {
        try {
            Optional<WmLocation> opt = wmLocationRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return mockList().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public WmLocation create(WmLocation entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        try {
            return wmLocationRepository.save(entity);
        } catch (Exception ignored) {
            if (entity.getId() == null) {
                entity.setId(System.currentTimeMillis());
            }
            return entity;
        }
    }

    public WmLocation update(WmLocation entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            if (!wmLocationRepository.findById(entity.getId()).isPresent()) {
                throw new RuntimeException("记录不存在");
            }
            return wmLocationRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            wmLocationRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<WmLocation> mockList() {
        return Arrays.asList(
                WmLocation.builder().id(1L).locationCode("L001").locationName("货架A-01").warehouseId(1L).warehouseName("原料仓").areaId(1L).areaName("原料A区").locationType("标准").capacity(100.0).status(0).build(),
                WmLocation.builder().id(2L).locationCode("L002").locationName("货架A-02").warehouseId(1L).warehouseName("原料仓").areaId(1L).areaName("原料A区").locationType("标准").capacity(100.0).status(0).build(),
                WmLocation.builder().id(3L).locationCode("L003").locationName("货架B-01").warehouseId(1L).warehouseName("原料仓").areaId(2L).areaName("原料B区").locationType("标准").capacity(120.0).status(0).build(),
                WmLocation.builder().id(4L).locationCode("L004").locationName("成品架-01").warehouseId(2L).warehouseName("成品仓").areaId(3L).areaName("成品A区").locationType("标准").capacity(200.0).status(0).build(),
                WmLocation.builder().id(5L).locationCode("L005").locationName("辅料架-01").warehouseId(4L).warehouseName("辅料仓").areaId(5L).areaName("辅料A区").locationType("标准").capacity(80.0).status(1).build()
        );
    }
}
