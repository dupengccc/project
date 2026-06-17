package com.mes.admin.modules.mes.pro.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ProBoardService {

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("todayOrders", 156);
        stats.put("todayOutput", 12850);
        stats.put("todayQualifiedRate", 0.975);
        stats.put("deviceUtilization", 0.86);
        return stats;
    }
}
