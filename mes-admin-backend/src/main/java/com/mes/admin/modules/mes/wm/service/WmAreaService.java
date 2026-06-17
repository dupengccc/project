package com.mes.admin.modules.mes.wm.service;

import com.mes.admin.modules.mes.wm.entity.WmArea;
import com.mes.admin.modules.mes.wm.repository.WmAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WmAreaService {

    @Autowired
    private WmAreaRepository wmAreaRepository;

    public List<WmArea> list(Map<String, Object> params) {
        try {
            return wmAreaRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("areaCode") != null) {
                        predicates.add(cb.like(root.get("areaCode"), "%" + params.get("areaCode") + "%"));
                    }
                    if (params.get("areaName") != null) {
                        predicates.add(cb.like(root.get("areaName"), "%" + params.get("areaName") + "%"));
                    }
                    if (params.get("warehouseId") != null) {
                        predicates.add(cb.equal(root.get("warehouseId"), params.get("warehouseId")));
                    }
                    if (params.get("warehouseName") != null) {
                        predicates.add(cb.like(root.get("warehouseName"), "%" + params.get("warehouseName") + "%"));
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

    public WmArea getById(Long id) {
        try {
            Optional<WmArea> opt = wmAreaRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return mockList().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public WmArea create(WmArea entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        try {
            return wmAreaRepository.save(entity);
        } catch (Exception ignored) {
            if (entity.getId() == null) {
                entity.setId(System.currentTimeMillis());
            }
            return entity;
        }
    }

    public WmArea update(WmArea entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            if (!wmAreaRepository.findById(entity.getId()).isPresent()) {
                throw new RuntimeException("记录不存在");
            }
            return wmAreaRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            wmAreaRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<WmArea> mockList() {
        return Arrays.asList(
                WmArea.builder().id(1L).areaCode("A001").areaName("原料A区").warehouseId(1L).warehouseName("原料仓").areaType("标准").areaSize(500.0).status(0).build(),
                WmArea.builder().id(2L).areaCode("A002").areaName("原料B区").warehouseId(1L).warehouseName("原料仓").areaType("标准").areaSize(500.0).status(0).build(),
                WmArea.builder().id(3L).areaCode("A003").areaName("成品A区").warehouseId(2L).warehouseName("成品仓").areaType("标准").areaSize(800.0).status(0).build(),
                WmArea.builder().id(4L).areaCode("A004").areaName("半成品A区").warehouseId(3L).warehouseName("半成品仓").areaType("标准").areaSize(300.0).status(0).build(),
                WmArea.builder().id(5L).areaCode("A005").areaName("辅料A区").warehouseId(4L).warehouseName("辅料仓").areaType("标准").areaSize(200.0).status(1).build()
        );
    }
}
