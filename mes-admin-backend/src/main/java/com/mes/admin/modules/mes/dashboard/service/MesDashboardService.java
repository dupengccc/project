package com.mes.admin.modules.mes.dashboard.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MesDashboardService {

    public Map<String, Object> getStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        Map<String, Object> coreMetrics = new LinkedHashMap<>();
        coreMetrics.put("todayOrders", 156);
        coreMetrics.put("todayOutput", 12850);
        coreMetrics.put("qualifiedRate", 0.975);
        coreMetrics.put("deviceUtilization", 0.86);
        coreMetrics.put("pendingReports", 23);
        coreMetrics.put("lowStockMaterials", 8);
        result.put("coreMetrics", coreMetrics);

        List<Map<String, Object>> productionProgress = new ArrayList<>();
        String[] orderCodes = {"WO20260100", "WO20260101", "WO20260102", "WO20260103", "WO20260104"};
        String[] productNames = {"不锈钢轴承 A201", "齿轮箱体 GB-300", "电机端盖 MD-100", "法兰盘 FL-050", "连接轴 LS-800"};
        double[] progress = {0.85, 0.62, 0.45, 0.30, 1.00};
        String[] statuses = {"生产中", "生产中", "生产中", "未开始", "已完成"};
        for (int i = 0; i < 5; i++) {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("orderCode", orderCodes[i]);
            p.put("productName", productNames[i]);
            p.put("progressPercent", progress[i]);
            p.put("status", statuses[i]);
            productionProgress.add(p);
        }
        result.put("productionProgress", productionProgress);

        List<Map<String, Object>> stockDistribution = new ArrayList<>();
        String[] warehouses = {"一号仓库", "二号仓库", "三号仓库", "原材料库", "成品库"};
        String[] locations = {"A-01", "B-01", "C-01", "D-01", "E-01"};
        double[] ratios = {0.25, 0.20, 0.18, 0.22, 0.15};
        for (int i = 0; i < 5; i++) {
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("warehouse", warehouses[i]);
            s.put("location", locations[i]);
            s.put("ratio", ratios[i]);
            stockDistribution.add(s);
        }
        result.put("stockDistribution", stockDistribution);

        List<Map<String, Object>> recentQuality = new ArrayList<>();
        double[] rates = {0.962, 0.971, 0.958, 0.980, 0.975, 0.968, 0.978};
        for (int i = 0; i < 7; i++) {
            Map<String, Object> q = new LinkedHashMap<>();
            q.put("day", "Day" + (i + 1));
            q.put("rate", rates[i]);
            recentQuality.add(q);
        }
        result.put("recentQuality", recentQuality);

        Map<String, Object> deviceStatus = new LinkedHashMap<>();
        deviceStatus.put("running", 42);
        deviceStatus.put("stopped", 5);
        deviceStatus.put("maintenance", 3);
        result.put("deviceStatus", deviceStatus);

        return result;
    }
}
