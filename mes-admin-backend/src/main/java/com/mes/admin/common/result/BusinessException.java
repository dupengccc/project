package com.mes.admin.common.result;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Integer code;
    private final String msg;

    public BusinessException(String msg) {
        super(msg);
        this.code = ResultCode.ERROR.getCode();
        this.msg = msg;
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
