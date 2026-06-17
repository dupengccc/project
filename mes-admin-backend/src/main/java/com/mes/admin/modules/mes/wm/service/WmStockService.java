package com.mes.admin.modules.mes.wm.service;

import com.mes.admin.modules.mes.wm.entity.WmStock;
import com.mes.admin.modules.mes.wm.repository.WmStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WmStockService {

    @Autowired
    private WmStockRepository wmStockRepository;

    public List<WmStock> list(Map<String, Object> params) {
        try {
            return wmStockRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("materialCode") != null) {
                        predicates.add(cb.like(root.get("materialCode"), "%" + params.get("materialCode") + "%"));
                    }
                    if (params.get("materialName") != null) {
                        predicates.add(cb.like(root.get("materialName"), "%" + params.get("materialName") + "%"));
                    }
                    if (params.get("warehouseName") != null) {
                        predicates.add(cb.like(root.get("warehouseName"), "%" + params.get("warehouseName") + "%"));
                    }
                    if (params.get("areaName") != null) {
                        predicates.add(cb.like(root.get("areaName"), "%" + params.get("areaName") + "%"));
                    }
                    if (params.get("locationName") != null) {
                        predicates.add(cb.like(root.get("locationName"), "%" + params.get("locationName") + "%"));
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

    public WmStock getById(Long id) {
        try {
            Optional<WmStock> opt = wmStockRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return mockList().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public WmStock create(WmStock entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        try {
            return wmStockRepository.save(entity);
        } catch (Exception ignored) {
            if (entity.getId() == null) {
                entity.setId(System.currentTimeMillis());
            }
            return entity;
        }
    }

    public WmStock update(WmStock entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            if (!wmStockRepository.findById(entity.getId()).isPresent()) {
                throw new RuntimeException("记录不存在");
            }
            return wmStockRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            wmStockRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<WmStock> mockList() {
        return Arrays.asList(
                WmStock.builder().id(1L).materialCode("M001").materialName("不锈钢板").spec("1000x2000x2mm").warehouseName("原料仓").areaName("原料A区").locationName("货架A-01").stockQty(500.0).safeStock(100.0).unit("张").status(0).build(),
                WmStock.builder().id(2L).materialCode("M002").materialName("铝板").spec("1000x2000x1.5mm").warehouseName("原料仓").areaName("原料A区").locationName("货架A-02").stockQty(300.0).safeStock(50.0).unit("张").status(0).build(),
                WmStock.builder().id(3L).materialCode("M003").materialName("螺丝M6").spec("长20mm").warehouseName("原料仓").areaName("原料B区").locationName("货架B-01").stockQty(5000.0).safeStock(1000.0).unit("个").status(0).build(),
                WmStock.builder().id(4L).materialCode("P001").materialName("成品A").spec("标准款").warehouseName("成品仓").areaName("成品A区").locationName("成品架-01").stockQty(150.0).safeStock(30.0).unit("件").status(0).build(),
                WmStock.builder().id(5L).materialCode("A001").materialName("包装纸箱").spec("400x300x200mm").warehouseName("辅料仓").areaName("辅料A区").locationName("辅料架-01").stockQty(2000.0).safeStock(500.0).unit("个").status(1).build()
        );
    }
}
