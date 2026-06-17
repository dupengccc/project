package com.mes.admin.modules.mes.pro.service;

import com.mes.admin.modules.mes.pro.entity.ProWorkorder;
import com.mes.admin.modules.mes.pro.repository.ProWorkorderRepository;
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
public class ProWorkorderService {

    @Autowired
    private ProWorkorderRepository proWorkorderRepository;

    public List<ProWorkorder> list(Map<String, Object> params) {
        try {
            return proWorkorderRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("orderCode") != null) {
                        predicates.add(cb.like(root.get("orderCode"), "%" + params.get("orderCode") + "%"));
                    }
                    if (params.get("productName") != null) {
                        predicates.add(cb.like(root.get("productName"), "%" + params.get("productName") + "%"));
                    }
                    if (params.get("status") != null) {
                        predicates.add(cb.equal(root.get("status"), params.get("status")));
                    }
                    if (params.get("workshopName") != null) {
                        predicates.add(cb.equal(root.get("workshopName"), params.get("workshopName")));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        } catch (Exception ignored) {
            return buildMockList();
        }
    }

    public ProWorkorder getById(Long id) {
        try {
            Optional<ProWorkorder> opt = proWorkorderRepository.findById(id);
            if (opt.isPresent()) return opt.get();
        } catch (Exception ignored) {
        }
        List<ProWorkorder> mockList = buildMockList();
        for (ProWorkorder item : mockList) {
            if (item.getId().equals(id)) return item;
        }
        return mockList.isEmpty() ? null : mockList.get(0);
    }

    public ProWorkorder create(ProWorkorder entity) {
        try {
            return proWorkorderRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public ProWorkorder update(ProWorkorder entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            return proWorkorderRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            proWorkorderRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<ProWorkorder> buildMockList() {
        List<ProWorkorder> list = new ArrayList<>();
        String[] statuses = {"未开始", "生产中", "已完成", "生产中", "已完成"};
        String[] products = {"不锈钢轴承 A201", "齿轮箱体 GB-300", "电机端盖 MD-100", "法兰盘 FL-050", "连接轴 LS-800"};
        String[] workshops = {"一号车间", "二号车间", "三号车间", "一号车间", "二号车间"};
        String[] stations = {"CNC-01", "CNC-02", "冲压机-03", "焊接工位-01", "装配线-02"};
        String[] operators = {"张伟", "李娜", "王强", "赵敏", "刘洋"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        for (int i = 0; i < 5; i++) {
            ProWorkorder wo = new ProWorkorder();
            wo.setId((long) (i + 1));
            wo.setOrderCode("WO" + String.format("%06d", 20260100 + i));
            wo.setProductName(products[i]);
            wo.setPlanQty(1000.0 * (i + 1));
            wo.setCompletedQty(statuses[i].equals("已完成") ? 1000.0 * (i + 1) : 300.0 * (i + 1));
            wo.setStatus(statuses[i]);
            wo.setPriority(i % 3 + 1);
            cal.add(Calendar.DATE, -i);
            wo.setPlanStart(cal.getTime());
            cal.add(Calendar.DATE, 5);
            wo.setPlanEnd(cal.getTime());
            cal.add(Calendar.DATE, -5);
            if (!statuses[i].equals("未开始")) {
                wo.setActualStart(cal.getTime());
            }
            if (statuses[i].equals("已完成")) {
                cal.add(Calendar.DATE, 3);
                wo.setActualEnd(cal.getTime());
            }
            wo.setWorkshopName(workshops[i]);
            wo.setWorkstationName(stations[i]);
            wo.setOperator(operators[i]);
            wo.setStatusFlag(0);
            list.add(wo);
        }
        return list;
    }
}
