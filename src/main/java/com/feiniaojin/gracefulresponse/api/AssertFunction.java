package com.feiniaojin.gracefulresponse.api;

/**
 * @author qinyujie
 */
@FunctionalInterface
public interface AssertFunction {
    /**
     * 执行断言判断逻辑，应抛出运行时异常
     */
    void doAssert();
}
