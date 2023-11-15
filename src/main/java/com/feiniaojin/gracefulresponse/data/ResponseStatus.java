package com.feiniaojin.gracefulresponse.data;

/**
 * @author <a href="mailto:qinyujie@gingo.cn">Yujie</a>
 * @version 0.1
 */
public interface ResponseStatus {
    /**
     * 设置响应码.
     *
     * @param code 设置的响应码.
     */
    void setCode(String code);

    /**
     * 获得响应码.
     *
     */
    String getCode();

    /**
     * 设置响应提示信息.
     *
     * @param msg 设置响应提示信息.
     */
    void setMsg(String msg);

    /**
     * 获得响应信息.
     *
     * @return
     */
    String getMsg();
}
