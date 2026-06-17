package com.mes.admin.modules.mes.dashboard.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.mes.dashboard.service.MesDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/mes/dashboard")
public class MesDashboardController {

    @Autowired
    private MesDashboardService mesDashboardService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.success(mesDashboardService.getStats());
    }
}
