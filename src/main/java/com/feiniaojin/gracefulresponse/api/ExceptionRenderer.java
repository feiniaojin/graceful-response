package com.feiniaojin.gracefulresponse.api;

import com.feiniaojin.gracefulresponse.data.Response;

/**
 * 异常消息渲染器
 *
 * @author cp
 * @since 2023-02-22 11:09
 */
public interface ExceptionRenderer {

    Response render(Throwable clazz, ResponseFactory responseFactory, ResponseStatusFactory responseStatusFactory);

}
