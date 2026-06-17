package com.mes.admin.modules.mes.cal.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.cal.entity.CalShift;
import com.mes.admin.modules.mes.cal.service.CalShiftService;
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
@RequestMapping("/api/mes/cal/shifts")
public class CalShiftController {

    @Autowired
    private CalShiftService calShiftService;

    @GetMapping
    public Result<PageUtil.PageResult<CalShift>> list(
            @RequestParam(required = false) String shiftCode,
            @RequestParam(required = false) String shiftName,
            @RequestParam(required = false) String shiftType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (shiftCode != null) params.put("shiftCode", shiftCode);
        if (shiftName != null) params.put("shiftName", shiftName);
        if (shiftType != null) params.put("shiftType", shiftType);
        if (status != null) params.put("status", status);
        List<CalShift> list = calShiftService.findAll(params);
        return Result.success(PageUtil.toPage(list, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<CalShift> getById(@PathVariable Long id) {
        return Result.success(calShiftService.findById(id));
    }

    @PostMapping
    public Result<CalShift> create(@RequestBody CalShift entity) {
        return Result.success(calShiftService.create(entity));
    }

    @PutMapping
    public Result<CalShift> update(@RequestBody CalShift entity) {
        return Result.success(calShiftService.update(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        calShiftService.delete(id);
        return Result.success();
    }
}
