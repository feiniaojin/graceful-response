package com.feiniaojin.gracefulresponse.test.app.vo;

import com.feiniaojin.gracefulresponse.api.ExcludeFromGracefulResponse;

import java.io.Serializable;

@ExcludeFromGracefulResponse
public class Result implements Serializable {
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
