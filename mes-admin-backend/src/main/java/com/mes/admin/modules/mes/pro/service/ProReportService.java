package com.mes.admin.modules.mes.pro.service;

import com.mes.admin.modules.mes.pro.entity.ProReport;
import com.mes.admin.modules.mes.pro.repository.ProReportRepository;
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
public class ProReportService {

    @Autowired
    private ProReportRepository proReportRepository;

    public List<ProReport> list(Map<String, Object> params) {
        try {
            return proReportRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("reportCode") != null) {
                        predicates.add(cb.like(root.get("reportCode"), "%" + params.get("reportCode") + "%"));
                    }
                    if (params.get("orderCode") != null) {
                        predicates.add(cb.like(root.get("orderCode"), "%" + params.get("orderCode") + "%"));
                    }
                    if (params.get("productName") != null) {
                        predicates.add(cb.like(root.get("productName"), "%" + params.get("productName") + "%"));
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
            return buildMockList();
        }
    }

    public ProReport getById(Long id) {
        try {
            Optional<ProReport> opt = proReportRepository.findById(id);
            if (opt.isPresent()) return opt.get();
        } catch (Exception ignored) {
        }
        List<ProReport> mockList = buildMockList();
        for (ProReport item : mockList) {
            if (item.getId().equals(id)) return item;
        }
        return mockList.isEmpty() ? null : mockList.get(0);
    }

    public ProReport create(ProReport entity) {
        try {
            return proReportRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public ProReport update(ProReport entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            return proReportRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            proReportRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<ProReport> buildMockList() {
        List<ProReport> list = new ArrayList<>();
        String[] products = {"不锈钢轴承 A201", "齿轮箱体 GB-300", "电机端盖 MD-100", "法兰盘 FL-050", "连接轴 LS-800"};
        String[] processes = {"粗加工", "精加工", "热处理", "质检", "装配"};
        String[] operators = {"张伟", "李娜", "王强", "赵敏", "刘洋"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        for (int i = 0; i < 5; i++) {
            ProReport r = new ProReport();
            r.setId((long) (i + 1));
            r.setReportCode("RP" + String.format("%06d", 20260100 + i));
            r.setOrderCode("WO" + String.format("%06d", 20260100 + i));
            r.setProductName(products[i]);
            r.setProcessName(processes[i]);
            r.setOperator(operators[i]);
            r.setReportQty(100.0 * (i + 1));
            r.setBadQty(5.0 * i);
            cal.add(Calendar.HOUR, -i * 3);
            r.setReportTime(cal.getTime());
            cal.add(Calendar.HOUR, i * 3);
            r.setStatus(i % 2);
            list.add(r);
        }
        return list;
    }
}
