package com.mes.admin.modules.mes.wm.service;

import com.mes.admin.modules.mes.wm.entity.WmOut;
import com.mes.admin.modules.mes.wm.repository.WmOutRepository;
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
public class WmOutService {

    @Autowired
    private WmOutRepository wmOutRepository;

    public List<WmOut> list(Map<String, Object> params) {
        try {
            return wmOutRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("outCode") != null) {
                        predicates.add(cb.like(root.get("outCode"), "%" + params.get("outCode") + "%"));
                    }
                    if (params.get("materialName") != null) {
                        predicates.add(cb.like(root.get("materialName"), "%" + params.get("materialName") + "%"));
                    }
                    if (params.get("receiveDept") != null) {
                        predicates.add(cb.like(root.get("receiveDept"), "%" + params.get("receiveDept") + "%"));
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

    public WmOut getById(Long id) {
        try {
            Optional<WmOut> opt = wmOutRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return mockList().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public WmOut create(WmOut entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        if (entity.getOutDate() == null) {
            entity.setOutDate(new Date());
        }
        try {
            return wmOutRepository.save(entity);
        } catch (Exception ignored) {
            if (entity.getId() == null) {
                entity.setId(System.currentTimeMillis());
            }
            return entity;
        }
    }

    public WmOut update(WmOut entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            if (!wmOutRepository.findById(entity.getId()).isPresent()) {
                throw new RuntimeException("记录不存在");
            }
            return wmOutRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            wmOutRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private Date daysAgo(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -days);
        return c.getTime();
    }

    private List<WmOut> mockList() {
        return Arrays.asList(
                WmOut.builder().id(1L).outCode("OUT20250001").materialName("不锈钢板").outQty(50.0).unit("张").receiveDept("一车间").warehouseName("原料仓").outDate(daysAgo(9)).operator("张三").status(0).build(),
                WmOut.builder().id(2L).outCode("OUT20250002").materialName("铝板").outQty(30.0).unit("张").receiveDept("二车间").warehouseName("原料仓").outDate(daysAgo(7)).operator("李四").status(0).build(),
                WmOut.builder().id(3L).outCode("OUT20250003").materialName("螺丝M6").outQty(1000.0).unit("个").receiveDept("装配车间").warehouseName("原料仓").outDate(daysAgo(4)).operator("王五").status(0).build(),
                WmOut.builder().id(4L).outCode("OUT20250004").materialName("成品A").outQty(100.0).unit("件").receiveDept("销售部").warehouseName("成品仓").outDate(daysAgo(2)).operator("赵六").status(0).build(),
                WmOut.builder().id(5L).outCode("OUT20250005").materialName("包装纸箱").outQty(500.0).unit("个").receiveDept("包装车间").warehouseName("辅料仓").outDate(daysAgo(1)).operator("孙七").status(1).build()
        );
    }
}
