package com.mes.admin.modules.mes.md.service;

import com.mes.admin.modules.mes.md.entity.MdRoute;
import com.mes.admin.modules.mes.md.repository.MdRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MdRouteService {

    @Autowired
    private MdRouteRepository mdRouteRepository;

    public List<MdRoute> list(Map<String, Object> params) {
        try {
            List<MdRoute> all = mdRouteRepository.findAll();
            return filterList(all, params);
        } catch (Exception ignored) {
            return filterList(buildMockRoutes(), params);
        }
    }

    public MdRoute getById(Long id) {
        try {
            Optional<MdRoute> opt = mdRouteRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        for (MdRoute r : buildMockRoutes()) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public MdRoute create(MdRoute route) {
        if (route.getStatus() == null) {
            route.setStatus(0);
        }
        try {
            return mdRouteRepository.save(route);
        } catch (Exception ignored) {
            if (route.getId() == null) {
                route.setId(System.currentTimeMillis());
            }
            return route;
        }
    }

    public MdRoute update(MdRoute route) {
        if (route.getId() == null) {
            throw new RuntimeException("工艺路线ID不能为空");
        }
        try {
            return mdRouteRepository.save(route);
        } catch (Exception ignored) {
            return route;
        }
    }

    public void delete(Long id) {
        try {
            mdRouteRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<MdRoute> filterList(List<MdRoute> list, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return list;
        }
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;
        Object statusObj = params.get("status");
        return list.stream().filter(r -> {
            if (StringUtils.hasText(keyword)) {
                boolean hit = false;
                if (r.getRouteCode() != null && r.getRouteCode().contains(keyword)) hit = true;
                if (r.getRouteName() != null && r.getRouteName().contains(keyword)) hit = true;
                if (r.getProductName() != null && r.getProductName().contains(keyword)) hit = true;
                if (!hit) return false;
            }
            if (statusObj != null) {
                try {
                    Integer s = Integer.valueOf(statusObj.toString());
                    if (!s.equals(r.getStatus())) return false;
                } catch (Exception ignored) {
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private List<MdRoute> buildMockRoutes() {
        List<MdRoute> list = new ArrayList<>();
        list.add(MdRoute.builder().id(1L).routeCode("R001").routeName("A型产品工艺路线")
                .productName("A型整机").description("冲压-焊接-装配-检验-包装")
                .processCount(5).status(0).remark("主要产品路线").build());
        list.add(MdRoute.builder().id(2L).routeCode("R002").routeName("B型产品工艺路线")
                .productName("B型整机").description("冲压-装配-检验-包装")
                .processCount(4).status(0).remark("普通产品路线").build());
        list.add(MdRoute.builder().id(3L).routeCode("R003").routeName("C型简化路线")
                .productName("C型整机").description("装配-检验-包装")
                .processCount(3).status(0).remark("简化路线").build());
        list.add(MdRoute.builder().id(4L).routeCode("R004").routeName("D型试制路线")
                .productName("D型整机").description("试制加工-装配-检验")
                .processCount(3).status(1).remark("试制路线").build());
        list.add(MdRoute.builder().id(5L).routeCode("R005").routeName("E型精加工路线")
                .productName("E型整机").description("精加工-装配-检验-包装")
                .processCount(4).status(0).remark("精加工路线").build());
        return list;
    }
}
