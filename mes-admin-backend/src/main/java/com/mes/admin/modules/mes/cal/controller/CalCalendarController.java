package com.mes.admin.modules.mes.cal.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.cal.entity.CalCalendar;
import com.mes.admin.modules.mes.cal.service.CalCalendarService;
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
@RequestMapping("/api/mes/cal/calendars")
public class CalCalendarController {

    @Autowired
    private CalCalendarService calCalendarService;

    @GetMapping
    public Result<PageUtil.PageResult<CalCalendar>> list(
            @RequestParam(required = false) String calendarCode,
            @RequestParam(required = false) String teamName,
            @RequestParam(required = false) String shiftName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (calendarCode != null) params.put("calendarCode", calendarCode);
        if (teamName != null) params.put("teamName", teamName);
        if (shiftName != null) params.put("shiftName", shiftName);
        List<CalCalendar> list = calCalendarService.findAll(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/byMonth")
    public Result<List<CalCalendar>> byMonth(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return Result.success(calCalendarService.findByMonth(year, month));
    }

    @GetMapping("/{id}")
    public Result<CalCalendar> getById(@PathVariable Long id) {
        return Result.success(calCalendarService.findById(id));
    }

    @PostMapping
    public Result<CalCalendar> create(@RequestBody CalCalendar entity) {
        return Result.success(calCalendarService.create(entity));
    }

    @PutMapping
    public Result<CalCalendar> update(@RequestBody CalCalendar entity) {
        return Result.success(calCalendarService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        calCalendarService.delete(id);
        return Result.success();
    }
}
