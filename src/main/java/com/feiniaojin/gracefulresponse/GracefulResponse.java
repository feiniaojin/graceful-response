package com.feiniaojin.gracefulresponse;

import com.feiniaojin.gracefulresponse.api.AssertFunction;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;

/**
 * GracefulResponse工具类
 *
 * @author qinyujie
 */
public class GracefulResponse {

    private GracefulResponse(){
        throw new IllegalStateException("Utility class");
    }

    /**
     * 需要抛自定义异常时，调用该方法
     *
     * @param code 异常码
     * @param msg  异常提示
     */
    public static void raiseException(String code, String msg) {
        throw new GracefulResponseException(code, msg);
    }

    /**
     * 需要抛自定义异常时，调用该方法
     */
    public static void raiseException(ResponseStatus responseStatus) {
        throw new GracefulResponseException(responseStatus.getCode(), responseStatus.getMsg());
    }

    /**
     * 需要抛自定义异常时，调用该方法
     *
     * @param code      异常码
     * @param msg       异常提示
     * @param throwable 捕获的异常
     */
    public static void raiseException(String code, String msg, Throwable throwable) {
        if (throwable instanceof GracefulResponseException gracefulResponseException) {
            gracefulResponseException.setCode(code);
            gracefulResponseException.setMsg(msg);
            throw gracefulResponseException;
        }
        throw new GracefulResponseException(code, msg, throwable);
    }

    /**
     * 需要抛自定义异常时，调用该方法
     */
    public static void raiseException(ResponseStatus responseStatus, Throwable throwable) {
        if (throwable instanceof GracefulResponseException gracefulResponseException) {
            gracefulResponseException.setCode(responseStatus.getCode());
            gracefulResponseException.setMsg(responseStatus.getMsg());
            throw gracefulResponseException;
        }
        throw new GracefulResponseException(responseStatus.getCode(), responseStatus.getMsg(), throwable);
    }

    public static void wrapAssert(AssertFunction assertFunction) {
        try {
            assertFunction.doAssert();
        } catch (Exception e) {
            throw new GracefulResponseException(e.getMessage(), e);
        }
    }

    public static void wrapAssert(String code, AssertFunction assertFunction) {
        try {
            assertFunction.doAssert();
        } catch (Exception e) {
            throw new GracefulResponseException(code, e.getMessage(), e);
        }
    }

    public static void wrapAssert(ResponseStatus responseStatus, AssertFunction assertFunction) {
        try {
            assertFunction.doAssert();
        } catch (Exception e) {
            throw new GracefulResponseException(responseStatus, e);
        }
    }

    public static void wrapAssert(String code, Object data, AssertFunction assertFunction) {
        try {
            assertFunction.doAssert();
        } catch (Exception e) {
            throw new GracefulResponseDataException(code, e.getMessage(), e, data);
        }
    }


}
