package com.mes.admin.modules.mes.md.service;

import com.mes.admin.modules.mes.md.entity.MdProcess;
import com.mes.admin.modules.mes.md.repository.MdProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MdProcessService {

    @Autowired
    private MdProcessRepository mdProcessRepository;

    public List<MdProcess> list(Map<String, Object> params) {
        try {
            List<MdProcess> all = mdProcessRepository.findAll();
            return filterList(all, params);
        } catch (Exception ignored) {
            return filterList(buildMockProcesses(), params);
        }
    }

    public MdProcess getById(Long id) {
        try {
            Optional<MdProcess> opt = mdProcessRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        for (MdProcess p : buildMockProcesses()) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public MdProcess create(MdProcess process) {
        if (process.getStatus() == null) {
            process.setStatus(0);
        }
        try {
            return mdProcessRepository.save(process);
        } catch (Exception ignored) {
            if (process.getId() == null) {
                process.setId(System.currentTimeMillis());
            }
            return process;
        }
    }

    public MdProcess update(MdProcess process) {
        if (process.getId() == null) {
            throw new RuntimeException("工序ID不能为空");
        }
        try {
            return mdProcessRepository.save(process);
        } catch (Exception ignored) {
            return process;
        }
    }

    public void delete(Long id) {
        try {
            mdProcessRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<MdProcess> filterList(List<MdProcess> list, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return list;
        }
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;
        String processType = params.get("processType") != null ? params.get("processType").toString() : null;
        Object statusObj = params.get("status");
        return list.stream().filter(p -> {
            if (StringUtils.hasText(keyword)) {
                boolean hit = false;
                if (p.getProcessCode() != null && p.getProcessCode().contains(keyword)) hit = true;
                if (p.getProcessName() != null && p.getProcessName().contains(keyword)) hit = true;
                if (!hit) return false;
            }
            if (StringUtils.hasText(processType) && !processType.equals(p.getProcessType())) {
                return false;
            }
            if (statusObj != null) {
                try {
                    Integer s = Integer.valueOf(statusObj.toString());
                    if (!s.equals(p.getStatus())) return false;
                } catch (Exception ignored) {
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private List<MdProcess> buildMockProcesses() {
        List<MdProcess> list = new ArrayList<>();
        list.add(MdProcess.builder().id(1L).processCode("P001").processName("冲压加工")
                .processType("加工").standardTime(15.0).status(0).remark("标准加工工序").build());
        list.add(MdProcess.builder().id(2L).processCode("P002").processName("焊接加工")
                .processType("加工").standardTime(20.0).status(0).remark("焊接工序").build());
        list.add(MdProcess.builder().id(3L).processCode("P003").processName("部件装配")
                .processType("装配").standardTime(25.0).status(0).remark("装配工序").build());
        list.add(MdProcess.builder().id(4L).processCode("P004").processName("质量检测")
                .processType("检验").standardTime(10.0).status(0).remark("检验工序").build());
        list.add(MdProcess.builder().id(5L).processCode("P005").processName("成品包装")
                .processType("包装").standardTime(8.0).status(1).remark("包装工序").build());
        return list;
    }
}
