package com.feiniaojin.gracefulresponse;

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
}
