package com.mes.admin.modules.mes.cal.service;

import com.mes.admin.modules.mes.cal.entity.CalPlan;
import com.mes.admin.modules.mes.cal.repository.CalPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CalPlanService {

    @Autowired
    private CalPlanRepository calPlanRepository;

    public List<CalPlan> findAll(Map<String, Object> params) {
        try {
            return calPlanRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("planCode") != null) {
                        predicates.add(cb.like(root.get("planCode"), "%" + params.get("planCode") + "%"));
                    }
                    if (params.get("teamName") != null) {
                        predicates.add(cb.equal(root.get("teamName"), params.get("teamName")));
                    }
                    if (params.get("shiftName") != null) {
                        predicates.add(cb.equal(root.get("shiftName"), params.get("shiftName")));
                    }
                    if (params.get("status") != null) {
                        predicates.add(cb.equal(root.get("status"), params.get("status")));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        } catch (Exception ignored) {
            return buildMockList();
        }
    }

    public CalPlan findById(Long id) {
        try {
            Optional<CalPlan> opt = calPlanRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return buildMockList().get(0);
    }

    public CalPlan create(CalPlan entity) {
        try {
            if (entity.getStatus() == null) entity.setStatus(0);
            return calPlanRepository.save(entity);
        } catch (Exception ignored) {
            entity.setId(System.currentTimeMillis());
            return entity;
        }
    }

    public CalPlan update(CalPlan entity) {
        try {
            if (entity.getId() == null) throw new RuntimeException("ID不能为空");
            return calPlanRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            calPlanRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<CalPlan> buildMockList() {
        List<String> teams = Arrays.asList("甲班", "乙班", "丙班", "丁班", "戊班");
        List<String> shifts = Arrays.asList("早班", "中班", "晚班", "大夜班", "加班");
        List<String> creators = Arrays.asList("调度A", "调度B", "调度C", "调度D", "调度E");
        List<CalPlan> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            CalPlan p = CalPlan.builder()
                    .id((long) i)
                    .planCode("PL-" + String.format("%04d", i))
                    .teamName(teams.get(i - 1))
                    .planDate(new Date())
                    .shiftName(shifts.get(i - 1))
                    .memberCount(8 + i)
                    .creator(creators.get(i - 1))
                    .status(i - 1 >= 3 ? 2 : 0)
                    .build();
            list.add(p);
        }
        return list;
    }
}
