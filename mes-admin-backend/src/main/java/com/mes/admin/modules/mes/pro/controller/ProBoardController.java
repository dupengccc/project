package com.mes.admin.modules.mes.pro.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.mes.pro.service.ProBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/mes/pro/board")
public class ProBoardController {

    @Autowired
    private ProBoardService proBoardService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.success(proBoardService.getStats());
    }
}
