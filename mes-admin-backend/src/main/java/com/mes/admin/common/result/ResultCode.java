package com.mes.admin.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(0, "success"),
    ERROR(500, "error"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    BAD_REQUEST(400, "参数错误"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在");

    private final Integer code;
    private final String msg;
}
