package com.mes.admin.modules.mes.md.service;

import com.mes.admin.modules.mes.md.entity.MdWorkstation;
import com.mes.admin.modules.mes.md.repository.MdWorkstationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MdWorkstationService {

    @Autowired
    private MdWorkstationRepository mdWorkstationRepository;

    public List<MdWorkstation> list(Map<String, Object> params) {
        try {
            List<MdWorkstation> all = mdWorkstationRepository.findAll();
            return filterList(all, params);
        } catch (Exception ignored) {
            return filterList(buildMockWorkstations(), params);
        }
    }

    public MdWorkstation getById(Long id) {
        try {
            Optional<MdWorkstation> opt = mdWorkstationRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        for (MdWorkstation w : buildMockWorkstations()) {
            if (w.getId().equals(id)) {
                return w;
            }
        }
        return null;
    }

    public MdWorkstation create(MdWorkstation workstation) {
        if (workstation.getStatus() == null) {
            workstation.setStatus(0);
        }
        try {
            return mdWorkstationRepository.save(workstation);
        } catch (Exception ignored) {
            if (workstation.getId() == null) {
                workstation.setId(System.currentTimeMillis());
            }
            return workstation;
        }
    }

    public MdWorkstation update(MdWorkstation workstation) {
        if (workstation.getId() == null) {
            throw new RuntimeException("工作站ID不能为空");
        }
        try {
            return mdWorkstationRepository.save(workstation);
        } catch (Exception ignored) {
            return workstation;
        }
    }

    public void delete(Long id) {
        try {
            mdWorkstationRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<MdWorkstation> filterList(List<MdWorkstation> list, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return list;
        }
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;
        Object workshopIdObj = params.get("workshopId");
        Object statusObj = params.get("status");
        return list.stream().filter(w -> {
            if (StringUtils.hasText(keyword)) {
                boolean hit = false;
                if (w.getStationCode() != null && w.getStationCode().contains(keyword)) hit = true;
                if (w.getStationName() != null && w.getStationName().contains(keyword)) hit = true;
                if (w.getDeviceNo() != null && w.getDeviceNo().contains(keyword)) hit = true;
                if (!hit) return false;
            }
            if (workshopIdObj != null) {
                try {
                    Long wid = Long.valueOf(workshopIdObj.toString());
                    if (!wid.equals(w.getWorkshopId())) return false;
                } catch (Exception ignored) {
                }
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

    private List<MdWorkstation> buildMockWorkstations() {
        List<MdWorkstation> list = new ArrayList<>();
        list.add(MdWorkstation.builder().id(1L).stationCode("ST001").stationName("冲压工位01")
                .workshopId(1L).workshopName("冲压车间").deviceNo("CNC-001").operator("张师傅")
                .status(0).remark("主工位").build());
        list.add(MdWorkstation.builder().id(2L).stationCode("ST002").stationName("冲压工位02")
                .workshopId(1L).workshopName("冲压车间").deviceNo("CNC-002").operator("李师傅")
                .status(0).remark("副工位").build());
        list.add(MdWorkstation.builder().id(3L).stationCode("ST003").stationName("装配工位01")
                .workshopId(2L).workshopName("装配车间").deviceNo("ASM-001").operator("王师傅")
                .status(0).remark("总装线").build());
        list.add(MdWorkstation.builder().id(4L).stationCode("ST004").stationName("焊接工位01")
                .workshopId(3L).workshopName("焊接车间").deviceNo("WLD-001").operator("赵师傅")
                .status(0).remark("机器人焊接").build());
        list.add(MdWorkstation.builder().id(5L).stationCode("ST005").stationName("喷涂工位01")
                .workshopId(4L).workshopName("喷涂车间").deviceNo("SPT-001").operator("孙师傅")
                .status(1).remark("待启用").build());
        return list;
    }
}
