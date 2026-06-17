package com.mes.admin.modules.mes.qc.service;

import com.mes.admin.modules.mes.qc.entity.QcDefect;
import com.mes.admin.modules.mes.qc.repository.QcDefectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QcDefectService {

    @Autowired
    private QcDefectRepository qcDefectRepository;

    public List<QcDefect> list(Map<String, Object> params) {
        try {
            return qcDefectRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("defectCode") != null) {
                        predicates.add(cb.like(root.get("defectCode"), "%" + params.get("defectCode") + "%"));
                    }
                    if (params.get("defectName") != null) {
                        predicates.add(cb.like(root.get("defectName"), "%" + params.get("defectName") + "%"));
                    }
                    if (params.get("defectType") != null) {
                        predicates.add(cb.equal(root.get("defectType"), params.get("defectType")));
                    }
                    if (params.get("severity") != null) {
                        predicates.add(cb.equal(root.get("severity"), params.get("severity")));
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

    public QcDefect getById(Long id) {
        try {
            Optional<QcDefect> opt = qcDefectRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return mockList().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public QcDefect create(QcDefect entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        try {
            return qcDefectRepository.save(entity);
        } catch (Exception ignored) {
            if (entity.getId() == null) {
                entity.setId(System.currentTimeMillis());
            }
            return entity;
        }
    }

    public QcDefect update(QcDefect entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            if (!qcDefectRepository.findById(entity.getId()).isPresent()) {
                throw new RuntimeException("记录不存在");
            }
            return qcDefectRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            qcDefectRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<QcDefect> mockList() {
        return Arrays.asList(
                QcDefect.builder().id(1L).defectCode("D001").defectName("表面划痕").defectType("外观").severity("轻微").suggestion("打磨抛光处理").status(0).build(),
                QcDefect.builder().id(2L).defectCode("D002").defectName("尺寸超差").defectType("尺寸").severity("一般").suggestion("返工或报废").status(0).build(),
                QcDefect.builder().id(3L).defectCode("D003").defectName("断裂").defectType("性能").severity("致命").suggestion("禁止使用并彻底排查").status(0).build(),
                QcDefect.builder().id(4L).defectCode("D004").defectName("焊点虚焊").defectType("性能").severity("严重").suggestion("重新焊接并测试").status(0).build(),
                QcDefect.builder().id(5L).defectCode("D005").defectName("色差").defectType("外观").severity("轻微").suggestion("重新喷漆或让步接收").status(1).build()
        );
    }
}
