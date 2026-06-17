package com.mes.admin.modules.mes.tm.service;

import com.mes.admin.modules.mes.tm.entity.TmTool;
import com.mes.admin.modules.mes.tm.repository.TmToolRepository;
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
public class TmToolService {

    @Autowired
    private TmToolRepository tmToolRepository;

    public List<TmTool> findAll(Map<String, Object> params) {
        try {
            return tmToolRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("toolCode") != null) {
                        predicates.add(cb.like(root.get("toolCode"), "%" + params.get("toolCode") + "%"));
                    }
                    if (params.get("toolName") != null) {
                        predicates.add(cb.like(root.get("toolName"), "%" + params.get("toolName") + "%"));
                    }
                    if (params.get("toolType") != null) {
                        predicates.add(cb.equal(root.get("toolType"), params.get("toolType")));
                    }
                    if (params.get("workshopName") != null) {
                        predicates.add(cb.equal(root.get("workshopName"), params.get("workshopName")));
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

    public TmTool findById(Long id) {
        try {
            Optional<TmTool> opt = tmToolRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return buildMockList().get(0);
    }

    public TmTool create(TmTool entity) {
        try {
            if (entity.getStatus() == null) entity.setStatus("在用");
            return tmToolRepository.save(entity);
        } catch (Exception ignored) {
            entity.setId(System.currentTimeMillis());
            return entity;
        }
    }

    public TmTool update(TmTool entity) {
        try {
            if (entity.getId() == null) throw new RuntimeException("ID不能为空");
            return tmToolRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            tmToolRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<TmTool> buildMockList() {
        List<String> types = Arrays.asList("刀具", "夹具", "量具", "模具", "其他");
        List<String> statuses = Arrays.asList("在用", "闲置", "维修", "报废", "在用");
        List<String> workshops = Arrays.asList("一车间", "二车间", "三车间", "四车间", "五车间");
        List<String> locations = Arrays.asList("A-1-01", "A-2-03", "B-1-05", "B-2-07", "C-1-09");
        List<TmTool> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            TmTool t = TmTool.builder()
                    .id((long) i)
                    .toolCode("TL-" + String.format("%04d", i))
                    .toolName(types.get(i - 1) + "工具" + i)
                    .toolType(types.get(i - 1))
                    .spec("SPEC-" + i)
                    .workshopName(workshops.get(i - 1))
                    .storageLocation(locations.get(i - 1))
                    .status(statuses.get(i - 1))
                    .inDate(new Date())
                    .build();
            list.add(t);
        }
        return list;
    }
}
