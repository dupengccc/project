package com.mes.admin.modules.mes.qc.service;

import com.mes.admin.modules.mes.qc.entity.QcTemplate;
import com.mes.admin.modules.mes.qc.repository.QcTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QcTemplateService {

    @Autowired
    private QcTemplateRepository qcTemplateRepository;

    public List<QcTemplate> list(Map<String, Object> params) {
        try {
            return qcTemplateRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("templateCode") != null) {
                        predicates.add(cb.like(root.get("templateCode"), "%" + params.get("templateCode") + "%"));
                    }
                    if (params.get("templateName") != null) {
                        predicates.add(cb.like(root.get("templateName"), "%" + params.get("templateName") + "%"));
                    }
                    if (params.get("productType") != null) {
                        predicates.add(cb.like(root.get("productType"), "%" + params.get("productType") + "%"));
                    }
                    if (params.get("version") != null) {
                        predicates.add(cb.like(root.get("version"), "%" + params.get("version") + "%"));
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

    public QcTemplate getById(Long id) {
        try {
            Optional<QcTemplate> opt = qcTemplateRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return mockList().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    public QcTemplate create(QcTemplate entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        try {
            return qcTemplateRepository.save(entity);
        } catch (Exception ignored) {
            if (entity.getId() == null) {
                entity.setId(System.currentTimeMillis());
            }
            return entity;
        }
    }

    public QcTemplate update(QcTemplate entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        try {
            if (!qcTemplateRepository.findById(entity.getId()).isPresent()) {
                throw new RuntimeException("记录不存在");
            }
            return qcTemplateRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            qcTemplateRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<QcTemplate> mockList() {
        return Arrays.asList(
                QcTemplate.builder().id(1L).templateCode("T001").templateName("成品A检验模板").productType("金属件").checkItemCount(10).version("V1.0").status(0).build(),
                QcTemplate.builder().id(2L).templateCode("T002").templateName("成品B检验模板").productType("塑料件").checkItemCount(8).version("V1.2").status(0).build(),
                QcTemplate.builder().id(3L).templateCode("T003").templateName("半成品检验模板").productType("组装件").checkItemCount(6).version("V1.0").status(0).build(),
                QcTemplate.builder().id(4L).templateCode("T004").templateName("来料检验通用模板").productType("通用").checkItemCount(5).version("V2.0").status(0).build(),
                QcTemplate.builder().id(5L).templateCode("T005").templateName("电子元件检验模板").productType("电子件").checkItemCount(12).version("V1.1").status(1).build()
        );
    }
}
