package com.feiniaojin.gracefulresponse;

public class GracefulResponseException extends RuntimeException {

    /**
     * 响应码
     */
    private String code;
    /**
     * 提示信息
     */
    private String msg;

    public GracefulResponseException() {
    }

    public GracefulResponseException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public GracefulResponseException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public GracefulResponseException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    public GracefulResponseException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
