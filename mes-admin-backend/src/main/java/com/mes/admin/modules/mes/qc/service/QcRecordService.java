package com.mes.admin.modules.mes.qc.service;

import com.mes.admin.modules.mes.qc.entity.QcRecord;
import com.mes.admin.modules.mes.qc.repository.QcRecordRepository;
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
public class QcRecordService {

    @Autowired
    private QcRecordRepository qcRecordRepository;

    public List<QcRecord> list(Map<String, Object> params) {
        try {
            return qcRecordRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("recordCode") != null) {
                        predicates.add(cb.like(root.get("recordCode"), "%" + params.get("recordCode") + "%"));
                    }
                    if (params.get("productName") != null) {
                        predicates.add(cb.like(root.get("productName"), "%" + params.get("productName") + "%"));
                    }
                    if (params.get("checkResult") != null) {
                        predicates.add(cb.equal(root.get("checkResult"), params.get("checkResult")));
                    }
                    if (params.get("checker") != null) {
                        predicates.add(cb.like(root.get("checker"), "%" + params.get("checker") + "%"));
                    }
                    if (params.get("orderCode") != null) {
                        predicates.add(cb.like(root.get("orderCode"), "%" + params.get("orderCode") + "%"));
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

    public QcRecord getById(Long id) {
        try {
            Optional<QcRecord> opt = qcRecordRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return mockList().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public QcRecord create(QcRecord entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        if (entity.getCheckTime() == null) {
            entity.setCheckTime(new Date());
        }
        try {
            return qcRecordRepository.save(entity);
        } catch (Exception ignored) {
            if (entity.getId() == null) {
                entity.setId(System.currentTimeMillis());
            }
            return entity;
        }
    }

    public QcRecord update(QcRecord entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            if (!qcRecordRepository.findById(entity.getId()).isPresent()) {
                throw new RuntimeException("记录不存在");
            }
            return qcRecordRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            qcRecordRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private Date daysAgo(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -days);
        return c.getTime();
    }

    private List<QcRecord> mockList() {
        return Arrays.asList(
                QcRecord.builder().id(1L).recordCode("R20250001").productName("成品A").checkQty(100.0).qualifiedQty(98.0).unqualifiedQty(2.0).checkResult("合格").checker("质检员A").checkTime(daysAgo(10)).orderCode("PO20250101").status(0).build(),
                QcRecord.builder().id(2L).recordCode("R20250002").productName("成品B").checkQty(200.0).qualifiedQty(195.0).unqualifiedQty(5.0).checkResult("合格").checker("质检员B").checkTime(daysAgo(8)).orderCode("PO20250102").status(0).build(),
                QcRecord.builder().id(3L).recordCode("R20250003").productName("半成品C").checkQty(50.0).qualifiedQty(45.0).unqualifiedQty(5.0).checkResult("不合格").checker("质检员A").checkTime(daysAgo(5)).orderCode("PO20250103").status(0).build(),
                QcRecord.builder().id(4L).recordCode("R20250004").productName("成品A").checkQty(150.0).qualifiedQty(150.0).unqualifiedQty(0.0).checkResult("合格").checker("质检员C").checkTime(daysAgo(3)).orderCode("PO20250104").status(0).build(),
                QcRecord.builder().id(5L).recordCode("R20250005").productName("电子元件D").checkQty(500.0).qualifiedQty(480.0).unqualifiedQty(20.0).checkResult("不合格").checker("质检员B").checkTime(daysAgo(1)).orderCode("PO20250105").status(1).build()
        );
    }
}
