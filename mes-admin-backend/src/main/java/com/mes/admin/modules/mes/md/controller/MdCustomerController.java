package com.mes.admin.modules.mes.md.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.common.util.PageUtil;
import com.mes.admin.modules.mes.md.entity.MdCustomer;
import com.mes.admin.modules.mes.md.service.MdCustomerService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/mes/md/customers")
public class MdCustomerController {

    @Autowired
    private MdCustomerService mdCustomerService;

    @GetMapping
    public Result<PageUtil.PageResult<MdCustomer>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String creditLevel,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (keyword != null) params.put("keyword", keyword);
        if (creditLevel != null) params.put("creditLevel", creditLevel);
        if (status != null) params.put("status", status);
        return Result.success(PageUtil.toPage(mdCustomerService.list(params), page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MdCustomer> getById(@PathVariable Long id) {
        return Result.success(mdCustomerService.getById(id));
    }

    @PostMapping
    public Result<MdCustomer> create(@RequestBody MdCustomer customer) {
        return Result.success(mdCustomerService.create(customer));
    }

    @PutMapping
    public Result<MdCustomer> update(@RequestBody MdCustomer customer) {
        return Result.success(mdCustomerService.update(customer));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mdCustomerService.delete(id);
        return Result.success();
    }
}
