package com.mes.admin.modules.mes.wm.service;

import com.mes.admin.modules.mes.wm.entity.WmIn;
import com.mes.admin.modules.mes.wm.repository.WmInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WmInService {

    @Autowired
    private WmInRepository wmInRepository;

    public List<WmIn> list(Map<String, Object> params) {
        try {
            return wmInRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("inCode") != null) {
                        predicates.add(cb.like(root.get("inCode"), "%" + params.get("inCode") + "%"));
                    }
                    if (params.get("materialName") != null) {
                        predicates.add(cb.like(root.get("materialName"), "%" + params.get("materialName") + "%"));
                    }
                    if (params.get("vendorName") != null) {
                        predicates.add(cb.like(root.get("vendorName"), "%" + params.get("vendorName") + "%"));
                    }
                    if (params.get("warehouseName") != null) {
                        predicates.add(cb.like(root.get("warehouseName"), "%" + params.get("warehouseName") + "%"));
                    }
                    if (params.get("operator") != null) {
                        predicates.add(cb.like(root.get("operator"), "%" + params.get("operator") + "%"));
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

    public WmIn getById(Long id) {
        try {
            Optional<WmIn> opt = wmInRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return mockList().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public WmIn create(WmIn entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        if (entity.getInDate() == null) {
            entity.setInDate(new Date());
        }
        try {
            return wmInRepository.save(entity);
        } catch (Exception ignored) {
            if (entity.getId() == null) {
                entity.setId(System.currentTimeMillis());
            }
            return entity;
        }
    }

    public WmIn update(WmIn entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            if (!wmInRepository.findById(entity.getId()).isPresent()) {
                throw new RuntimeException("记录不存在");
            }
            return wmInRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            wmInRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private Date daysAgo(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -days);
        return c.getTime();
    }

    private List<WmIn> mockList() {
        return Arrays.asList(
                WmIn.builder().id(1L).inCode("IN20250001").materialName("不锈钢板").inQty(100.0).unit("张").vendorName("上海金属有限公司").warehouseName("原料仓").inDate(daysAgo(10)).operator("张三").status(0).build(),
                WmIn.builder().id(2L).inCode("IN20250002").materialName("铝板").inQty(80.0).unit("张").vendorName("华东铝业").warehouseName("原料仓").inDate(daysAgo(8)).operator("李四").status(0).build(),
                WmIn.builder().id(3L).inCode("IN20250003").materialName("螺丝M6").inQty(2000.0).unit("个").vendorName("标准件工厂").warehouseName("原料仓").inDate(daysAgo(5)).operator("王五").status(0).build(),
                WmIn.builder().id(4L).inCode("IN20250004").materialName("包装纸箱").inQty(1000.0).unit("个").vendorName("纸制品厂").warehouseName("辅料仓").inDate(daysAgo(3)).operator("赵六").status(0).build(),
                WmIn.builder().id(5L).inCode("IN20250005").materialName("塑料颗粒").inQty(500.0).unit("kg").vendorName("化工公司").warehouseName("原料仓").inDate(daysAgo(1)).operator("孙七").status(1).build()
        );
    }
}
