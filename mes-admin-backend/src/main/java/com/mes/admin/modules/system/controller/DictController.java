package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.system.entity.SysDictData;
import com.mes.admin.modules.system.service.SysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/dict")
public class DictController {

    @Autowired
    private SysDictDataService sysDictDataService;

    @GetMapping("/data/type/{dictType}")
    public Result<List<SysDictData>> findByType(@PathVariable String dictType) {
        return Result.success(sysDictDataService.findByDictType(dictType));
    }

    @GetMapping("/data/list")
    public Result<List<SysDictData>> list() {
        return Result.success(sysDictDataService.findAll());
    }

    @GetMapping("/data/{id}")
    public Result<SysDictData> getById(@PathVariable Long id) {
        return Result.success(sysDictDataService.findById(id));
    }

    @PostMapping("/data")
    public Result<SysDictData> create(@RequestBody SysDictData dict) {
        return Result.success(sysDictDataService.create(dict));
    }

    @PutMapping("/data")
    public Result<SysDictData> update(@RequestBody SysDictData dict) {
        return Result.success(sysDictDataService.update(dict));
    }

    @DeleteMapping("/data/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysDictDataService.delete(id);
        return Result.success();
    }
}
