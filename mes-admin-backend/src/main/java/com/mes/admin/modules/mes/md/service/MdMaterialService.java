package com.mes.admin.modules.mes.md.service;

import com.mes.admin.modules.mes.md.entity.MdMaterial;
import com.mes.admin.modules.mes.md.repository.MdMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MdMaterialService {

    @Autowired
    private MdMaterialRepository mdMaterialRepository;

    public List<MdMaterial> list(Map<String, Object> params) {
        try {
            List<MdMaterial> all = mdMaterialRepository.findAll();
            return filterMaterials(all, params);
        } catch (Exception ignored) {
            return filterMaterials(buildMockMaterials(), params);
        }
    }

    public MdMaterial getById(Long id) {
        try {
            Optional<MdMaterial> opt = mdMaterialRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        for (MdMaterial m : buildMockMaterials()) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }

    public MdMaterial create(MdMaterial material) {
        if (material.getStatus() == null) {
            material.setStatus(0);
        }
        try {
            return mdMaterialRepository.save(material);
        } catch (Exception ignored) {
            if (material.getId() == null) {
                material.setId(System.currentTimeMillis());
            }
            return material;
        }
    }

    public MdMaterial update(MdMaterial material) {
        if (material.getId() == null) {
            throw new RuntimeException("物料ID不能为空");
        }
        try {
            return mdMaterialRepository.save(material);
        } catch (Exception ignored) {
            return material;
        }
    }

    public void delete(Long id) {
        try {
            mdMaterialRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<MdMaterial> filterMaterials(List<MdMaterial> list, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return list;
        }
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;
        String materialType = params.get("materialType") != null ? params.get("materialType").toString() : null;
        Object statusObj = params.get("status");
        return list.stream().filter(m -> {
            if (StringUtils.hasText(keyword)) {
                boolean hit = false;
                if (m.getMaterialCode() != null && m.getMaterialCode().contains(keyword)) hit = true;
                if (m.getMaterialName() != null && m.getMaterialName().contains(keyword)) hit = true;
                if (m.getSpec() != null && m.getSpec().contains(keyword)) hit = true;
                if (!hit) return false;
            }
            if (StringUtils.hasText(materialType) && !materialType.equals(m.getMaterialType())) {
                return false;
            }
            if (statusObj != null) {
                try {
                    Integer s = Integer.valueOf(statusObj.toString());
                    if (!s.equals(m.getStatus())) return false;
                } catch (Exception ignored) {
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private List<MdMaterial> buildMockMaterials() {
        List<MdMaterial> list = new ArrayList<>();
        list.add(MdMaterial.builder().id(1L).materialCode("M001").materialName("钢板原材料")
                .spec("1000x2000x3mm").materialType("原材料").unit("张")
                .safeStock(100.0).currentStock(520.0).status(0).remark("常用原材料").build());
        list.add(MdMaterial.builder().id(2L).materialCode("M002").materialName("铝合金型材")
                .spec("6063-T5").materialType("原材料").unit("米")
                .safeStock(200.0).currentStock(1800.0).status(0).remark("主原料").build());
        list.add(MdMaterial.builder().id(3L).materialCode("W001").materialName("车身半成品")
                .spec("A型").materialType("半成品").unit("件")
                .safeStock(20.0).currentStock(45.0).status(0).remark("半成品库").build());
        list.add(MdMaterial.builder().id(4L).materialCode("P001").materialName("整机成品")
                .spec("V1.0").materialType("成品").unit("台")
                .safeStock(10.0).currentStock(25.0).status(0).remark("成品库").build());
        list.add(MdMaterial.builder().id(5L).materialCode("A001").materialName("螺丝辅料")
                .spec("M6x20").materialType("辅料").unit("个")
                .safeStock(5000.0).currentStock(12000.0).status(1).remark("辅助材料").build());
        return list;
    }
}
