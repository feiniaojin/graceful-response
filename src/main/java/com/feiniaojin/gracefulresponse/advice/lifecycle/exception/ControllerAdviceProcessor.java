package com.feiniaojin.gracefulresponse.advice.lifecycle.exception;

import com.feiniaojin.gracefulresponse.data.Response;

/**
 * 异常处理器
 *
 * @author qinyujie
 */
public interface ControllerAdviceProcessor {
    Response process(Throwable throwable);
}
