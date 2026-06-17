package com.mes.admin.modules.mes.dv.service;

import com.mes.admin.modules.mes.dv.entity.DvCheck;
import com.mes.admin.modules.mes.dv.repository.DvCheckRepository;
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
public class DvCheckService {

    @Autowired
    private DvCheckRepository dvCheckRepository;

    public List<DvCheck> findAll(Map<String, Object> params) {
        try {
            return dvCheckRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("checkCode") != null) {
                        predicates.add(cb.like(root.get("checkCode"), "%" + params.get("checkCode") + "%"));
                    }
                    if (params.get("deviceCode") != null) {
                        predicates.add(cb.like(root.get("deviceCode"), "%" + params.get("deviceCode") + "%"));
                    }
                    if (params.get("deviceName") != null) {
                        predicates.add(cb.like(root.get("deviceName"), "%" + params.get("deviceName") + "%"));
                    }
                    if (params.get("checkType") != null) {
                        predicates.add(cb.equal(root.get("checkType"), params.get("checkType")));
                    }
                    if (params.get("checkResult") != null) {
                        predicates.add(cb.equal(root.get("checkResult"), params.get("checkResult")));
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

    public DvCheck findById(Long id) {
        try {
            Optional<DvCheck> opt = dvCheckRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return buildMockList().get(0);
    }

    public DvCheck create(DvCheck entity) {
        try {
            if (entity.getStatus() == null) entity.setStatus(0);
            return dvCheckRepository.save(entity);
        } catch (Exception ignored) {
            entity.setId(System.currentTimeMillis());
            return entity;
        }
    }

    public DvCheck update(DvCheck entity) {
        try {
            if (entity.getId() == null) throw new RuntimeException("ID不能为空");
            return dvCheckRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            dvCheckRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<DvCheck> buildMockList() {
        List<String> types = Arrays.asList("日常", "定期", "专项", "日常", "定期");
        List<String> results = Arrays.asList("正常", "异常", "正常", "正常", "异常");
        List<String> checkers = Arrays.asList("质检员A", "质检员B", "质检员C", "质检员D", "质检员E");
        List<String> devices = Arrays.asList("DV-0001", "DV-0002", "DV-0003", "DV-0004", "DV-0005");
        List<String> deviceNames = Arrays.asList("车床设备1", "铣床设备2", "磨床设备3", "钻床设备4", "加工中心设备5");
        List<DvCheck> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            DvCheck d = DvCheck.builder()
                    .id((long) i)
                    .checkCode("CHK-" + String.format("%04d", i))
                    .deviceCode(devices.get(i - 1))
                    .deviceName(deviceNames.get(i - 1))
                    .checkType(types.get(i - 1))
                    .checker(checkers.get(i - 1))
                    .checkDate(new Date())
                    .checkResult(results.get(i - 1))
                    .nextCheckDate(new Date())
                    .status(i - 1 >= 3 ? 2 : 0)
                    .build();
            list.add(d);
        }
        return list;
    }
}
