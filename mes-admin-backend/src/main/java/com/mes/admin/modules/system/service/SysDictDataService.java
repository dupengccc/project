package com.mes.admin.modules.system.service;

import com.mes.admin.modules.system.entity.SysDictData;
import com.mes.admin.modules.system.repository.SysDictDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SysDictDataService {

    @Autowired
    private SysDictDataRepository sysDictDataRepository;

    public List<SysDictData> findByDictType(String dictType) {
        try {
            return sysDictDataRepository.findByDictType(dictType);
        } catch (Exception ignored) {
            return buildMockDict(dictType);
        }
    }

    public List<SysDictData> findAll() {
        try {
            return sysDictDataRepository.findAll();
        } catch (Exception ignored) {
            List<SysDictData> list = new ArrayList<>();
            list.addAll(buildMockDict("sys_user_status"));
            list.addAll(buildMockDict("sys_org_type"));
            return list;
        }
    }

    public SysDictData findById(Long id) {
        try {
            Optional<SysDictData> opt = sysDictDataRepository.findById(id);
            return opt.orElse(null);
        } catch (Exception ignored) {
            return null;
        }
    }

    public SysDictData create(SysDictData dict) {
        if (dict.getStatus() == null) {
            dict.setStatus(0);
        }
        return sysDictDataRepository.save(dict);
    }

    public SysDictData update(SysDictData dict) {
        if (dict.getId() == null) {
            throw new RuntimeException("字典ID不能为空");
        }
        return sysDictDataRepository.save(dict);
    }

    public void delete(Long id) {
        sysDictDataRepository.deleteById(id);
    }

    private List<SysDictData> buildMockDict(String dictType) {
        List<SysDictData> list = new ArrayList<>();
        if ("sys_user_status".equals(dictType)) {
            SysDictData d1 = new SysDictData();
            d1.setId(1L);
            d1.setDictSort(1);
            d1.setDictLabel("启用");
            d1.setDictValue("0");
            d1.setDictType("sys_user_status");
            d1.setCssClass("");
            d1.setListClass("primary");
            d1.setIsDefault("Y");
            d1.setStatus(0);
            list.add(d1);

            SysDictData d2 = new SysDictData();
            d2.setId(2L);
            d2.setDictSort(2);
            d2.setDictLabel("禁用");
            d2.setDictValue("1");
            d2.setDictType("sys_user_status");
            d2.setCssClass("");
            d2.setListClass("danger");
            d2.setIsDefault("N");
            d2.setStatus(0);
            list.add(d2);
            return list;
        }
        if ("sys_org_type".equals(dictType)) {
            String[][] data = new String[][] {
                    {"集团", "group"}, {"分公司", "branch"}, {"部门", "dept"}
            };
            long id = 10;
            int sort = 1;
            for (String[] row : data) {
                SysDictData d = new SysDictData();
                d.setId(id++);
                d.setDictSort(sort++);
                d.setDictLabel(row[0]);
                d.setDictValue(row[1]);
                d.setDictType("sys_org_type");
                d.setListClass("info");
                d.setIsDefault("N");
                d.setStatus(0);
                list.add(d);
            }
            return list;
        }
        return Collections.emptyList();
    }
}
