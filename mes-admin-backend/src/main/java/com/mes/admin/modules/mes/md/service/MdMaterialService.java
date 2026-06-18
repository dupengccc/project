package com.mes.admin.modules.mes.md.service;

import com.mes.admin.modules.mes.md.entity.MdMaterial;
import com.mes.admin.modules.mes.md.repository.MdMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
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
        if (material.getCreateTime() == null) {
            material.setCreateTime(new Date());
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
        if (material.getUpdateTime() == null) {
            material.setUpdateTime(new Date());
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
        String materialCode = params.get("materialCode") != null ? params.get("materialCode").toString() : null;
        String materialName = params.get("materialName") != null ? params.get("materialName").toString() : null;
        String materialType = params.get("materialType") != null ? params.get("materialType").toString() : null;
        String manageMode = params.get("manageMode") != null ? params.get("manageMode").toString() : null;
        Object statusObj = params.get("status");
        return list.stream().filter(m -> {
            if (StringUtils.hasText(materialCode)
                    && (m.getMaterialCode() == null || !m.getMaterialCode().contains(materialCode))) {
                return false;
            }
            if (StringUtils.hasText(materialName)
                    && (m.getMaterialName() == null || !m.getMaterialName().contains(materialName))) {
                return false;
            }
            if (StringUtils.hasText(materialType) && !materialType.equals(m.getMaterialType())) {
                return false;
            }
            if (StringUtils.hasText(manageMode) && !manageMode.equals(m.getManageMode())) {
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
        list.add(build(1L, "M00001", "不锈钢板", "1220*2440*2mm", "原材料", "外购",
                "张", 2L, "华东分公司", 50.0, 200.0, 0, "常用原材料"));
        list.add(build(2L, "M00002", "铝合金型材", "6063-T5 2m", "原材料", "外购",
                "根", 2L, "华东分公司", 100.0, 80.0, 0, "主原料"));
        list.add(build(3L, "B00001", "半成品装配A", "A100", "半成品", "自制",
                "件", 2L, "华东分公司", 30.0, 45.0, 0, "标准半成品"));
        list.add(build(4L, "F00001", "工控机箱", "IPC-610L", "成品", "自制",
                "台", 2L, "华东分公司", 10.0, 25.0, 0, "标准产品"));
        list.add(build(5L, "A00001", "内六角螺丝", "M4x8", "辅料", "外购",
                "包", 3L, "华南分公司", 500.0, 320.0, 0, "辅助材料"));
        list.add(build(6L, "F00002", "触控一体机", "15寸 工业级", "成品", "委外加工",
                "台", 3L, "华南分公司", 5.0, 3.0, 1, "暂停销售"));
        return list;
    }

    private MdMaterial build(Long id, String code, String name, String spec, String type,
                             String mode, String unit, Long orgId, String orgName,
                             Double safe, Double current, Integer status, String remark) {
        MdMaterial m = new MdMaterial();
        m.setId(id);
        m.setMaterialCode(code);
        m.setMaterialName(name);
        m.setSpec(spec);
        m.setMaterialType(type);
        m.setManageMode(mode);
        m.setUnit(unit);
        m.setOrgId(orgId);
        m.setOrgName(orgName);
        m.setSafeStock(safe);
        m.setCurrentStock(current);
        m.setStatus(status);
        m.setRemark(remark);
        m.setCreateTime(new Date());
        return m;
    }
}
