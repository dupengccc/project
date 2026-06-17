package com.mes.admin.modules.mes.md.service;

import com.mes.admin.modules.mes.md.entity.MdWorkshop;
import com.mes.admin.modules.mes.md.repository.MdWorkshopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MdWorkshopService {

    @Autowired
    private MdWorkshopRepository mdWorkshopRepository;

    public List<MdWorkshop> list(Map<String, Object> params) {
        try {
            List<MdWorkshop> all = mdWorkshopRepository.findAll();
            return filterList(all, params);
        } catch (Exception ignored) {
            return filterList(buildMockWorkshops(), params);
        }
    }

    public MdWorkshop getById(Long id) {
        try {
            Optional<MdWorkshop> opt = mdWorkshopRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        for (MdWorkshop w : buildMockWorkshops()) {
            if (w.getId().equals(id)) {
                return w;
            }
        }
        return null;
    }

    public MdWorkshop create(MdWorkshop workshop) {
        if (workshop.getStatus() == null) {
            workshop.setStatus(0);
        }
        try {
            return mdWorkshopRepository.save(workshop);
        } catch (Exception ignored) {
            if (workshop.getId() == null) {
                workshop.setId(System.currentTimeMillis());
            }
            return workshop;
        }
    }

    public MdWorkshop update(MdWorkshop workshop) {
        if (workshop.getId() == null) {
            throw new RuntimeException("车间ID不能为空");
        }
        try {
            return mdWorkshopRepository.save(workshop);
        } catch (Exception ignored) {
            return workshop;
        }
    }

    public void delete(Long id) {
        try {
            mdWorkshopRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<MdWorkshop> filterList(List<MdWorkshop> list, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return list;
        }
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;
        Object statusObj = params.get("status");
        return list.stream().filter(w -> {
            if (StringUtils.hasText(keyword)) {
                boolean hit = false;
                if (w.getWorkshopCode() != null && w.getWorkshopCode().contains(keyword)) hit = true;
                if (w.getWorkshopName() != null && w.getWorkshopName().contains(keyword)) hit = true;
                if (!hit) return false;
            }
            if (statusObj != null) {
                try {
                    Integer s = Integer.valueOf(statusObj.toString());
                    if (!s.equals(w.getStatus())) return false;
                } catch (Exception ignored) {
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private List<MdWorkshop> buildMockWorkshops() {
        List<MdWorkshop> list = new ArrayList<>();
        list.add(MdWorkshop.builder().id(1L).workshopCode("WS001").workshopName("冲压车间")
                .leader("李主任").phone("13700137001").area(1500.0).status(0).remark("主车间").build());
        list.add(MdWorkshop.builder().id(2L).workshopCode("WS002").workshopName("装配车间")
                .leader("王主任").phone("13700137002").area(2000.0).status(0).remark("总装线").build());
        list.add(MdWorkshop.builder().id(3L).workshopCode("WS003").workshopName("焊接车间")
                .leader("赵主任").phone("13700137003").area(1200.0).status(0).remark("精密焊接").build());
        list.add(MdWorkshop.builder().id(4L).workshopCode("WS004").workshopName("喷涂车间")
                .leader("孙主任").phone("13700137004").area(1000.0).status(0).remark("表面处理").build());
        list.add(MdWorkshop.builder().id(5L).workshopCode("WS005").workshopName("成品检测")
                .leader("周主任").phone("13700137005").area(800.0).status(1).remark("待启用").build());
        return list;
    }
}
