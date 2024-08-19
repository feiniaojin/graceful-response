package com.feiniaojin.gracefulresponse.defaults;

import com.feiniaojin.gracefulresponse.data.ResponseStatus;

public enum DefaultResponseStatusEnum implements ResponseStatus {
    NOT_FOUND("1024", "2048");
    private final String code;
    private final String msg;

    DefaultResponseStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public void setCode(String code) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setMsg(String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
