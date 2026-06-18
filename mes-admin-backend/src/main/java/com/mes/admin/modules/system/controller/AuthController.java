package com.mes.admin.modules.system.controller;

import com.mes.admin.common.result.Result;
import com.mes.admin.modules.system.dto.ChangePasswordDTO;
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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private org.springframework.web.context.request.RequestContextHolder requestContextHolderRef;

    @Autowired
    private SysLoginLogService sysLoginLogService;

    /** 当前登录的日志ID（简单方案：以 ThreadLocal 存储 id 写入；真实环境可通过 Redis 维护 token -> logId） */
    private static final java.util.Map<String, Long> LOGIN_LOG_ID_MAP = new java.util.concurrent.ConcurrentHashMap<>();

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        Long logId = null;
        try {
            LoginRespDTO resp = sysUserService.login(dto);
            UserInfoDTO info = sysUserService.getUserInfo();
            logId = sysLoginLogService.logLogin(
                    dto.getUsername(),
                    info.getNickname() != null ? info.getNickname() : dto.getUsername(),
                    getCurrentIp(),
                    info.getOrgId(),
                    info.getOrgName(),
                    0,
                    "登录成功"
            );
            if (resp.getToken() != null) {
                LOGIN_LOG_ID_MAP.put(resp.getToken(), logId);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("token", resp.getToken());
            data.put("tokenType", resp.getTokenType());
            data.put("expiresIn", resp.getExpiresIn());
            data.put("userInfo", info);
            data.put("logId", logId);
            return Result.success(data);
        } catch (Exception ex) {
            sysLoginLogService.logLogin(
                    dto.getUsername(), dto.getUsername(), getCurrentIp(),
                    null, null, 1, ex.getMessage() == null ? "登录失败" : ex.getMessage());
            throw ex;
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        // 从请求头中读取 Bearer token，匹配到对应登录日志并更新退出时间
        try {
            String token = extractToken();
            Long logId = token != null ? LOGIN_LOG_ID_MAP.remove(token) : null;
            sysLoginLogService.logLogout(logId, null);
        } catch (Exception ignored) {}
        return Result.success();
    }

    private String extractToken() {
        try {
            org.springframework.web.context.request.ServletRequestAttributes attr =
                    (org.springframework.web.context.request.ServletRequestAttributes)
                            org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attr == null) return null;
            javax.servlet.http.HttpServletRequest req = attr.getRequest();
            String h = req.getHeader("Authorization");
            if (h != null && h.toLowerCase().startsWith("bearer ")) {
                return h.substring(7).trim();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String getCurrentIp() {
        try {
            org.springframework.web.context.request.ServletRequestAttributes attr =
                    (org.springframework.web.context.request.ServletRequestAttributes)
                            org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attr == null) return "127.0.0.1";
            javax.servlet.http.HttpServletRequest req = attr.getRequest();
            String x = req.getHeader("X-Forwarded-For");
            if (x != null && !x.isEmpty()) {
                int c = x.indexOf(',');
                return c > 0 ? x.substring(0, c).trim() : x.trim();
            }
            return req.getRemoteAddr();
        } catch (Exception ignored) {
            return "127.0.0.1";
        }
    }

    @GetMapping("/getInfo")
    public Result<UserInfoDTO> getInfo() {
        return Result.success(sysUserService.getUserInfo());
    }

    /**
     * 修改密码
     * 前端右上角下拉菜单 → 修改密码
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        sysUserService.changePassword(dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }
}
