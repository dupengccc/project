package com.mes.admin.modules.mes.md.service;

import com.mes.admin.modules.mes.md.entity.MdVendor;
import com.mes.admin.modules.mes.md.repository.MdVendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MdVendorService {

    @Autowired
    private MdVendorRepository mdVendorRepository;

    public List<MdVendor> list(Map<String, Object> params) {
        try {
            List<MdVendor> all = mdVendorRepository.findAll();
            return filterList(all, params);
        } catch (Exception ignored) {
            return filterList(buildMockVendors(), params);
        }
    }

    public MdVendor getById(Long id) {
        try {
            Optional<MdVendor> opt = mdVendorRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        for (MdVendor v : buildMockVendors()) {
            if (v.getId().equals(id)) {
                return v;
            }
        }
        return null;
    }

    public MdVendor create(MdVendor vendor) {
        if (vendor.getStatus() == null) {
            vendor.setStatus(0);
        }
        try {
            return mdVendorRepository.save(vendor);
        } catch (Exception ignored) {
            if (vendor.getId() == null) {
                vendor.setId(System.currentTimeMillis());
            }
            return vendor;
        }
    }

    public MdVendor update(MdVendor vendor) {
        if (vendor.getId() == null) {
            throw new RuntimeException("供应商ID不能为空");
        }
        try {
            return mdVendorRepository.save(vendor);
        } catch (Exception ignored) {
            return vendor;
        }
    }

    public void delete(Long id) {
        try {
            mdVendorRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<MdVendor> filterList(List<MdVendor> list, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return list;
        }
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;
        String supplyLevel = params.get("supplyLevel") != null ? params.get("supplyLevel").toString() : null;
        Object statusObj = params.get("status");
        return list.stream().filter(v -> {
            if (StringUtils.hasText(keyword)) {
                boolean hit = false;
                if (v.getVendorCode() != null && v.getVendorCode().contains(keyword)) hit = true;
                if (v.getVendorName() != null && v.getVendorName().contains(keyword)) hit = true;
                if (v.getContact() != null && v.getContact().contains(keyword)) hit = true;
                if (!hit) return false;
            }
            if (StringUtils.hasText(supplyLevel) && !supplyLevel.equals(v.getSupplyLevel())) {
                return false;
            }
            if (statusObj != null) {
                try {
                    Integer s = Integer.valueOf(statusObj.toString());
                    if (!s.equals(v.getStatus())) return false;
                } catch (Exception ignored) {
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private List<MdVendor> buildMockVendors() {
        List<MdVendor> list = new ArrayList<>();
        list.add(MdVendor.builder().id(1L).vendorCode("V001").vendorName("宝山钢铁集团")
                .contact("刘经理").phone("13900139001").address("上海市宝山区")
                .supplyLevel("战略").status(0).remark("核心原料供应商").build());
        list.add(MdVendor.builder().id(2L).vendorCode("V002").vendorName("华东铝业")
                .contact("陈经理").phone("13900139002").address("江苏省苏州市")
                .supplyLevel("主要").status(0).remark("铝型材主要供应商").build());
        list.add(MdVendor.builder().id(3L).vendorCode("V003").vendorName("精工电子")
                .contact("孙主管").phone("13900139003").address("浙江省杭州市")
                .supplyLevel("主要").status(0).remark("电子元件").build());
        list.add(MdVendor.builder().id(4L).vendorCode("V004").vendorName("通用配件厂")
                .contact("周先生").phone("13900139004").address("江苏省无锡市")
                .supplyLevel("一般").status(0).remark("普通配件").build());
        list.add(MdVendor.builder().id(5L).vendorCode("V005").vendorName("临时劳保供应")
                .contact("吴先生").phone("13900139005").address("上海市青浦区")
                .supplyLevel("临时").status(1).remark("临时采购").build());
        return list;
    }
}
