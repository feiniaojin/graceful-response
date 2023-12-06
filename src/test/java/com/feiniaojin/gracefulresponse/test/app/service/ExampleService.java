package com.feiniaojin.gracefulresponse.test.app.service;

/**
 * 测试Service接口.
 *
 * @author qinyujie
 */
public interface ExampleService {
    /**
     * 测试产生非受检异常的情形，即抛出运行时异常.
     */
    void testUnCheckedException();

    /**
     * 测试抛出受检异常的情况.
     *
     * @throws Exception 抛出受检异常
     */
    void testCheckedException() throws Exception;

}
