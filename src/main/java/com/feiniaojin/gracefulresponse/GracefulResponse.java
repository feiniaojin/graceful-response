package com.feiniaojin.gracefulresponse;

import com.feiniaojin.gracefulresponse.api.AssertFunction;

/**
 * GracefulResponse工具类
 */
public class GracefulResponse {

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
     *
     * @param code      异常码
     * @param msg       异常提示
     * @param throwable 捕获的异常
     */
    public static void raiseException(String code, String msg, Throwable throwable) {
        throw new GracefulResponseException(code, msg, throwable);
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
}
