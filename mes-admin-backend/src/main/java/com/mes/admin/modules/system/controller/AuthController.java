package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.system.dto.LoginDTO;
import com.mes.admin.modules.system.dto.LoginRespDTO;
import com.mes.admin.modules.system.dto.UserInfoDTO;
import com.mes.admin.modules.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        LoginRespDTO resp = sysUserService.login(dto);
        UserInfoDTO info = sysUserService.getUserInfo();
        Map<String, Object> data = new HashMap<>();
        data.put("token", resp.getToken());
        data.put("tokenType", resp.getTokenType());
        data.put("expiresIn", resp.getExpiresIn());
        data.put("userInfo", info);
        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/getInfo")
    public Result<UserInfoDTO> getInfo() {
        return Result.success(sysUserService.getUserInfo());
    }
}
