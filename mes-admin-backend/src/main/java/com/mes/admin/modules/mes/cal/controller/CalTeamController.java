package com.mes.admin.modules.mes.cal.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.cal.entity.CalTeam;
import com.mes.admin.modules.mes.cal.service.CalTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mes/cal/teams")
public class CalTeamController {

    @Autowired
    private CalTeamService calTeamService;

    @GetMapping
    public Result<PageUtil.PageResult<CalTeam>> list(
            @RequestParam(required = false) String teamCode,
            @RequestParam(required = false) String teamName,
            @RequestParam(required = false) String workshopName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (teamCode != null) params.put("teamCode", teamCode);
        if (teamName != null) params.put("teamName", teamName);
        if (workshopName != null) params.put("workshopName", workshopName);
        if (status != null) params.put("status", status);
        List<CalTeam> list = calTeamService.findAll(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<CalTeam> getById(@PathVariable Long id) {
        return Result.success(calTeamService.findById(id));
    }

    @PostMapping
    public Result<CalTeam> create(@RequestBody CalTeam entity) {
        return Result.success(calTeamService.create(entity));
    }

    @PutMapping
    public Result<CalTeam> update(@RequestBody CalTeam entity) {
        return Result.success(calTeamService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        calTeamService.delete(id);
        return Result.success();
    }
}
