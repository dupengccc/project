package com.mes.admin.modules.mes.dv.service;

import com.mes.admin.modules.mes.dv.entity.DvDevice;
import com.mes.admin.modules.mes.dv.repository.DvDeviceRepository;
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
public class DvDeviceService {

    @Autowired
    private DvDeviceRepository dvDeviceRepository;

    public List<DvDevice> findAll(Map<String, Object> params) {
        try {
            return dvDeviceRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("deviceCode") != null) {
                        predicates.add(cb.like(root.get("deviceCode"), "%" + params.get("deviceCode") + "%"));
                    }
                    if (params.get("deviceName") != null) {
                        predicates.add(cb.like(root.get("deviceName"), "%" + params.get("deviceName") + "%"));
                    }
                    if (params.get("deviceType") != null) {
                        predicates.add(cb.equal(root.get("deviceType"), params.get("deviceType")));
                    }
                    if (params.get("workshopId") != null) {
                        predicates.add(cb.equal(root.get("workshopId"), params.get("workshopId")));
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

    public DvDevice findById(Long id) {
        try {
            Optional<DvDevice> opt = dvDeviceRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return buildMockList().get(0);
    }

    public DvDevice create(DvDevice entity) {
        try {
            if (entity.getStatus() == null) entity.setStatus(0);
            return dvDeviceRepository.save(entity);
        } catch (Exception ignored) {
            entity.setId(System.currentTimeMillis());
            return entity;
        }
    }

    public DvDevice update(DvDevice entity) {
        try {
            if (entity.getId() == null) throw new RuntimeException("ID不能为空");
            return dvDeviceRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            dvDeviceRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<DvDevice> buildMockList() {
        List<String> types = Arrays.asList("车床", "铣床", "磨床", "钻床", "加工中心");
        List<String> workshops = Arrays.asList("一车间", "二车间", "三车间", "四车间", "五车间");
        List<String> leaders = Arrays.asList("张工", "李工", "王工", "赵工", "刘工");
        List<DvDevice> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            DvDevice d = DvDevice.builder()
                    .id((long) i)
                    .deviceCode("DV-" + String.format("%04d", i))
                    .deviceName(types.get(i - 1) + "设备" + i)
                    .deviceType(types.get(i - 1))
                    .spec("SPC-" + i)
                    .workshopId((long) i)
                    .workshopName(workshops.get(i - 1))
                    .leader(leaders.get(i - 1))
                    .purchaseDate(new Date())
                    .serviceYears(i)
                    .status(i - 1 >= 3 ? 2 : (i - 1))
                    .build();
            list.add(d);
        }
        return list;
    }
}
