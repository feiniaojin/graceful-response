package com.feiniaojin.gracefulresponse.data;

/**
 * @author <a href="mailto:qinyujie@gingo.cn">Yujie</a>
 * @version 0.1
 */
public class ResponseStatus {

    /**
     * 响应码.
     */
    private String code;

    /**
     * 响应信息.
     */
    private String msg;

    public ResponseStatus() {
    }

    /**
     * 通过响应码和响应信息构造枚举.
     *
     * @param code 响应码
     * @param msg  响应信息
     */
    public ResponseStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 设置响应码.
     *
     * @param code 设置的响应码.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获得响应码.
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置响应提示信息.
     *
     * @param msg 设置响应提示信息.
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获得响应信息.
     *
     * @return
     */
    public String getMsg() {
        return msg;
    }
}
