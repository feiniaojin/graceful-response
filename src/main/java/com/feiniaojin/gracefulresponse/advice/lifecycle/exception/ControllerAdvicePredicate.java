package com.feiniaojin.gracefulresponse.advice.lifecycle.exception;

/**
 * 处理之前需要判断是否需要处理
 *
 * @author qinyujie
 */
public interface ControllerAdvicePredicate {

    /**
     * 判断是否要处理
     *
     * @param throwable
     * @return
     */
    default boolean test(Throwable throwable) {
        return true;
    }
}
