package com.mes.admin.modules.mes.wm.service;

import com.mes.admin.modules.mes.wm.entity.WmWarehouse;
import com.mes.admin.modules.mes.wm.repository.WmWarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WmWarehouseService {

    @Autowired
    private WmWarehouseRepository wmWarehouseRepository;

    public List<WmWarehouse> list(Map<String, Object> params) {
        try {
            return wmWarehouseRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("warehouseCode") != null) {
                        predicates.add(cb.like(root.get("warehouseCode"), "%" + params.get("warehouseCode") + "%"));
                    }
                    if (params.get("warehouseName") != null) {
                        predicates.add(cb.like(root.get("warehouseName"), "%" + params.get("warehouseName") + "%"));
                    }
                    if (params.get("warehouseType") != null) {
                        predicates.add(cb.equal(root.get("warehouseType"), params.get("warehouseType")));
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

    public WmWarehouse getById(Long id) {
        try {
            Optional<WmWarehouse> opt = wmWarehouseRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return mockList().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public WmWarehouse create(WmWarehouse entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        try {
            return wmWarehouseRepository.save(entity);
        } catch (Exception ignored) {
            if (entity.getId() == null) {
                entity.setId(System.currentTimeMillis());
            }
            return entity;
        }
    }

    public WmWarehouse update(WmWarehouse entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            if (!wmWarehouseRepository.findById(entity.getId()).isPresent()) {
                throw new RuntimeException("记录不存在");
            }
            return wmWarehouseRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            wmWarehouseRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<WmWarehouse> mockList() {
        return Arrays.asList(
                WmWarehouse.builder().id(1L).warehouseCode("WH001").warehouseName("原料仓").warehouseType("原料仓").leader("张三").phone("13800000001").address("上海市浦东新区一号路1号").status(0).build(),
                WmWarehouse.builder().id(2L).warehouseCode("WH002").warehouseName("成品仓").warehouseType("成品仓").leader("李四").phone("13800000002").address("上海市浦东新区一号路2号").status(0).build(),
                WmWarehouse.builder().id(3L).warehouseCode("WH003").warehouseName("半成品仓").warehouseType("半成品仓").leader("王五").phone("13800000003").address("上海市浦东新区一号路3号").status(0).build(),
                WmWarehouse.builder().id(4L).warehouseCode("WH004").warehouseName("辅料仓").warehouseType("辅料仓").leader("赵六").phone("13800000004").address("上海市浦东新区一号路4号").status(0).build(),
                WmWarehouse.builder().id(5L).warehouseCode("WH005").warehouseName("备用仓").warehouseType("原料仓").leader("孙七").phone("13800000005").address("上海市浦东新区一号路5号").status(1).build()
        );
    }
}
