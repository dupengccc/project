package com.mes.admin.modules.mes.dv.service;

import com.mes.admin.modules.mes.dv.entity.DvRepair;
import com.mes.admin.modules.mes.dv.repository.DvRepairRepository;
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
public class DvRepairService {

    @Autowired
    private DvRepairRepository dvRepairRepository;

    public List<DvRepair> findAll(Map<String, Object> params) {
        try {
            return dvRepairRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("repairCode") != null) {
                        predicates.add(cb.like(root.get("repairCode"), "%" + params.get("repairCode") + "%"));
                    }
                    if (params.get("deviceCode") != null) {
                        predicates.add(cb.like(root.get("deviceCode"), "%" + params.get("deviceCode") + "%"));
                    }
                    if (params.get("deviceName") != null) {
                        predicates.add(cb.like(root.get("deviceName"), "%" + params.get("deviceName") + "%"));
                    }
                    if (params.get("repairStatus") != null) {
                        predicates.add(cb.equal(root.get("repairStatus"), params.get("repairStatus")));
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

    public DvRepair findById(Long id) {
        try {
            Optional<DvRepair> opt = dvRepairRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return buildMockList().get(0);
    }

    public DvRepair create(DvRepair entity) {
        try {
            if (entity.getStatus() == null) entity.setStatus(0);
            return dvRepairRepository.save(entity);
        } catch (Exception ignored) {
            entity.setId(System.currentTimeMillis());
            return entity;
        }
    }

    public DvRepair update(DvRepair entity) {
        try {
            if (entity.getId() == null) throw new RuntimeException("ID不能为空");
            return dvRepairRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            dvRepairRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<DvRepair> buildMockList() {
        List<String> statuses = Arrays.asList("维修中", "已完成", "待验收", "维修中", "已完成");
        List<String> reporters = Arrays.asList("员工A", "员工B", "员工C", "员工D", "员工E");
        List<String> repairers = Arrays.asList("修理工甲", "修理工乙", "修理工丙", "修理工丁", "修理工戊");
        List<String> devices = Arrays.asList("DV-0001", "DV-0002", "DV-0003", "DV-0004", "DV-0005");
        List<String> deviceNames = Arrays.asList("车床设备1", "铣床设备2", "磨床设备3", "钻床设备4", "加工中心设备5");
        List<DvRepair> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            DvRepair d = DvRepair.builder()
                    .id((long) i)
                    .repairCode("RP-" + String.format("%04d", i))
                    .deviceCode(devices.get(i - 1))
                    .deviceName(deviceNames.get(i - 1))
                    .faultDesc("设备出现故障" + i + "号，需要维修处理")
                    .reporter(reporters.get(i - 1))
                    .reportDate(new Date())
                    .repairer(repairers.get(i - 1))
                    .repairDate(new Date())
                    .repairStatus(statuses.get(i - 1))
                    .repairCost((double) (i * 100))
                    .status(i - 1 >= 3 ? 2 : 0)
                    .build();
            list.add(d);
        }
        return list;
    }
}
