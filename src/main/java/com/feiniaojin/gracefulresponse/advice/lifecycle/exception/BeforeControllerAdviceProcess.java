package com.feiniaojin.gracefulresponse.advice.lifecycle.exception;

/**
 * 接受异常的处理器
 *
 * @author qinyujie
 */
public interface BeforeControllerAdviceProcess {
    void call(Throwable throwable);
}
