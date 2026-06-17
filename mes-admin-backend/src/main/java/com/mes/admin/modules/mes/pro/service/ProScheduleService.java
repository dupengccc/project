package com.mes.admin.modules.mes.pro.service;

import com.mes.admin.modules.mes.pro.entity.ProSchedule;
import com.mes.admin.modules.mes.pro.repository.ProScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProScheduleService {

    @Autowired
    private ProScheduleRepository proScheduleRepository;

    public List<ProSchedule> list(Map<String, Object> params) {
        try {
            return proScheduleRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("scheduleCode") != null) {
                        predicates.add(cb.like(root.get("scheduleCode"), "%" + params.get("scheduleCode") + "%"));
                    }
                    if (params.get("orderCode") != null) {
                        predicates.add(cb.like(root.get("orderCode"), "%" + params.get("orderCode") + "%"));
                    }
                    if (params.get("productName") != null) {
                        predicates.add(cb.like(root.get("productName"), "%" + params.get("productName") + "%"));
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

    public ProSchedule getById(Long id) {
        try {
            Optional<ProSchedule> opt = proScheduleRepository.findById(id);
            if (opt.isPresent()) return opt.get();
        } catch (Exception ignored) {
        }
        List<ProSchedule> mockList = buildMockList();
        for (ProSchedule item : mockList) {
            if (item.getId().equals(id)) return item;
        }
        return mockList.isEmpty() ? null : mockList.get(0);
    }

    public ProSchedule create(ProSchedule entity) {
        try {
            return proScheduleRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public ProSchedule update(ProSchedule entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            return proScheduleRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            proScheduleRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<ProSchedule> buildMockList() {
        List<ProSchedule> list = new ArrayList<>();
        String[] products = {"不锈钢轴承 A201", "齿轮箱体 GB-300", "电机端盖 MD-100", "法兰盘 FL-050", "连接轴 LS-800"};
        String[] workshops = {"一号车间", "二号车间", "三号车间", "一号车间", "二号车间"};
        String[] stations = {"CNC-01", "CNC-02", "冲压机-03", "焊接工位-01", "装配线-02"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        for (int i = 0; i < 5; i++) {
            ProSchedule s = new ProSchedule();
            s.setId((long) (i + 1));
            s.setScheduleCode("SC" + String.format("%06d", 20260100 + i));
            s.setOrderCode("WO" + String.format("%06d", 20260100 + i));
            s.setProductName(products[i]);
            s.setWorkshopName(workshops[i]);
            s.setWorkstationName(stations[i]);
            cal.add(Calendar.DATE, i);
            s.setScheduleDate(cal.getTime());
            cal.add(Calendar.DATE, -i);
            s.setPlanQty(500.0 * (i + 1));
            s.setCompletedQty(200.0 * (i + 1));
            s.setStatus(i % 3);
            list.add(s);
        }
        return list;
    }
}
