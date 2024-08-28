package com.feiniaojin.gracefulresponse.advice.lifecycle.exception;

/**
 * 接受异常的处理器
 *
 * @author qinyujie
 */
public interface BeforeControllerAdviceProcess {

    /**
     * ControllerAdvice处理前的回调
     *
     * @param throwable
     */
    void call(Throwable throwable);
}
