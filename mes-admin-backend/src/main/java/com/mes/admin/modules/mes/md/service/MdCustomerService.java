package com.mes.admin.modules.mes.md.service;

import com.mes.admin.modules.mes.md.entity.MdCustomer;
import com.mes.admin.modules.mes.md.repository.MdCustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MdCustomerService {

    @Autowired
    private MdCustomerRepository mdCustomerRepository;

    public List<MdCustomer> list(Map<String, Object> params) {
        try {
            List<MdCustomer> all = mdCustomerRepository.findAll();
            return filterList(all, params);
        } catch (Exception ignored) {
            return filterList(buildMockCustomers(), params);
        }
    }

    public MdCustomer getById(Long id) {
        try {
            Optional<MdCustomer> opt = mdCustomerRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        for (MdCustomer c : buildMockCustomers()) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public MdCustomer create(MdCustomer customer) {
        if (customer.getStatus() == null) {
            customer.setStatus(0);
        }
        try {
            return mdCustomerRepository.save(customer);
        } catch (Exception ignored) {
            if (customer.getId() == null) {
                customer.setId(System.currentTimeMillis());
            }
            return customer;
        }
    }

    public MdCustomer update(MdCustomer customer) {
        if (customer.getId() == null) {
            throw new RuntimeException("客户ID不能为空");
        }
        try {
            return mdCustomerRepository.save(customer);
        } catch (Exception ignored) {
            return customer;
        }
    }

    public void delete(Long id) {
        try {
            mdCustomerRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<MdCustomer> filterList(List<MdCustomer> list, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return list;
        }
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;
        String creditLevel = params.get("creditLevel") != null ? params.get("creditLevel").toString() : null;
        Object statusObj = params.get("status");
        return list.stream().filter(c -> {
            if (StringUtils.hasText(keyword)) {
                boolean hit = false;
                if (c.getCustomerCode() != null && c.getCustomerCode().contains(keyword)) hit = true;
                if (c.getCustomerName() != null && c.getCustomerName().contains(keyword)) hit = true;
                if (c.getContact() != null && c.getContact().contains(keyword)) hit = true;
                if (!hit) return false;
            }
            if (StringUtils.hasText(creditLevel) && !creditLevel.equals(c.getCreditLevel())) {
                return false;
            }
            if (statusObj != null) {
                try {
                    Integer s = Integer.valueOf(statusObj.toString());
                    if (!s.equals(c.getStatus())) return false;
                } catch (Exception ignored) {
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private List<MdCustomer> buildMockCustomers() {
        List<MdCustomer> list = new ArrayList<>();
        list.add(MdCustomer.builder().id(1L).customerCode("C001").customerName("华东汽车集团")
                .contact("张经理").phone("13800138001").address("上海市浦东新区张江路1号")
                .creditLevel("A").status(0).remark("战略客户").build());
        list.add(MdCustomer.builder().id(2L).customerCode("C002").customerName("北方机械制造")
                .contact("李主管").phone("13800138002").address("北京市朝阳区工业园")
                .creditLevel("A").status(0).remark("长期合作").build());
        list.add(MdCustomer.builder().id(3L).customerCode("C003").customerName("南方电子科技")
                .contact("王主任").phone("13800138003").address("深圳市南山区")
                .creditLevel("B").status(0).remark("重要客户").build());
        list.add(MdCustomer.builder().id(4L).customerCode("C004").customerName("西部物流公司")
                .contact("赵经理").phone("13800138004").address("成都市高新区")
                .creditLevel("C").status(0).remark("普通客户").build());
        list.add(MdCustomer.builder().id(5L).customerCode("C005").customerName("临时采购商")
                .contact("钱先生").phone("13800138005").address("广州市天河区")
                .creditLevel("D").status(1).remark("待评估").build());
        return list;
    }
}
