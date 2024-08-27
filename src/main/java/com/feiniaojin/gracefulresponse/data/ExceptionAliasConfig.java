package com.feiniaojin.gracefulresponse.data;

/**
 * 外部异常配置
 *
 * @author qinyujie
 */
public class ExceptionAliasConfig {

    private String code;

    private String msg;

    private Integer httpStatusCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
