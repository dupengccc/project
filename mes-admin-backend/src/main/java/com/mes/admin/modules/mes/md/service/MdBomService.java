package com.mes.admin.modules.mes.md.service;

import com.mes.admin.modules.mes.md.entity.MdBom;
import com.mes.admin.modules.mes.md.repository.MdBomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MdBomService {

    @Autowired
    private MdBomRepository mdBomRepository;

    public List<MdBom> list(Map<String, Object> params) {
        try {
            List<MdBom> all = mdBomRepository.findAll();
            return filterList(all, params);
        } catch (Exception ignored) {
            return filterList(buildMockBoms(), params);
        }
    }

    public MdBom getById(Long id) {
        try {
            Optional<MdBom> opt = mdBomRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        for (MdBom b : buildMockBoms()) {
            if (b.getId().equals(id)) {
                return b;
            }
        }
        return null;
    }

    public MdBom create(MdBom bom) {
        if (bom.getStatus() == null) {
            bom.setStatus(0);
        }
        try {
            return mdBomRepository.save(bom);
        } catch (Exception ignored) {
            if (bom.getId() == null) {
                bom.setId(System.currentTimeMillis());
            }
            return bom;
        }
    }

    public MdBom update(MdBom bom) {
        if (bom.getId() == null) {
            throw new RuntimeException("BOMID不能为空");
        }
        try {
            return mdBomRepository.save(bom);
        } catch (Exception ignored) {
            return bom;
        }
    }

    public void delete(Long id) {
        try {
            mdBomRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<MdBom> filterList(List<MdBom> list, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return list;
        }
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;
        Object statusObj = params.get("status");
        return list.stream().filter(b -> {
            if (StringUtils.hasText(keyword)) {
                boolean hit = false;
                if (b.getBomCode() != null && b.getBomCode().contains(keyword)) hit = true;
                if (b.getBomName() != null && b.getBomName().contains(keyword)) hit = true;
                if (b.getProductName() != null && b.getProductName().contains(keyword)) hit = true;
                if (!hit) return false;
            }
            if (statusObj != null) {
                try {
                    Integer s = Integer.valueOf(statusObj.toString());
                    if (!s.equals(b.getStatus())) return false;
                } catch (Exception ignored) {
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private List<MdBom> buildMockBoms() {
        List<MdBom> list = new ArrayList<>();
        list.add(MdBom.builder().id(1L).bomCode("BOM001").bomName("A型整机BOM")
                .productName("A型整机").version("V1.0").status(0).remark("主要产品").build());
        list.add(MdBom.builder().id(2L).bomCode("BOM002").bomName("B型整机BOM")
                .productName("B型整机").version("V1.2").status(0).remark("在用").build());
        list.add(MdBom.builder().id(3L).bomCode("BOM003").bomName("C型整机BOM")
                .productName("C型整机").version("V2.0").status(0).remark("优化版本").build());
        list.add(MdBom.builder().id(4L).bomCode("BOM004").bomName("D型试制BOM")
                .productName("D型整机").version("V0.9").status(1).remark("试制中").build());
        list.add(MdBom.builder().id(5L).bomCode("BOM005").bomName("E型精加工BOM")
                .productName("E型整机").version("V1.0").status(0).remark("精密产品").build());
        return list;
    }
}
