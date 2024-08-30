package com.feiniaojin.gracefulresponse.advice.lifecycle.exception;

import com.feiniaojin.gracefulresponse.data.Response;

/**
 * 异常处理后的回调
 *
 * @author qinyujie
 */
public interface AfterControllerAdviceProcess {
    /**
     * 执行处理逻辑之后的回调
     *
     * @param response
     * @param throwable
     */
    void call(Response response, Throwable throwable);
}
