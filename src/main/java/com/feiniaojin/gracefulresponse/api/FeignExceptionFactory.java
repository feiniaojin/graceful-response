package com.feiniaojin.gracefulresponse.api;

import com.feiniaojin.gracefulresponse.data.Response;

/**
 * Feign 响应异常工厂
 *
 * @author cp
 * @since 2023-02-23 17:30
 */
public interface FeignExceptionFactory {

    RuntimeException newInstance(Response response);

}
