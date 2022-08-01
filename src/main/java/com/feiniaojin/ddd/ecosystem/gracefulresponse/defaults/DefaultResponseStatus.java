package com.feiniaojin.ddd.ecosystem.gracefulresponse.defaults;


import com.feiniaojin.ddd.ecosystem.gracefulresponse.data.ResponseStatus;

/**
 * <p>
 * 默认的响应码枚举类，通常用于成功和失败的场合.
 * </p>
 * <p>
 * 成功：{@code DEFAULT_SUCCESS}
 * </p>
 * <p>
 * 失败：{@code DEFAULT_FAIL}.
 * </p>
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
public class DefaultResponseStatus implements ResponseStatus {

    /**
     * {@code DEFAULT_SUCCESS} 请求处理成功.
     */
    public static final DefaultResponseStatus DEFAULT_SUCCESS = new DefaultResponseStatus("0", "ok");

    /**
     * {@code DEFAULT_FAIL} 请求处理失败.
     */
    public static final DefaultResponseStatus DEFAULT_FAIL = new DefaultResponseStatus("1", "error");

    /**
     * 响应码.
     */
    private String code;

    /**
     * 响应信息.
     */
    private String msg;

    public DefaultResponseStatus() {
    }

    /**
     * 通过响应码和响应信息构造枚举.
     *
     * @param code 响应码
     * @param msg  响应信息
     */
    public DefaultResponseStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
