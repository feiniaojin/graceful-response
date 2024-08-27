package com.feiniaojin.gracefulresponse.advice.lifecycle.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

/**
 * 异常处理器
 *
 * @author qinyujie
 */
public interface ResponseBodyAdviceProcessor {

    Object process(Object body,
                     MethodParameter returnType,
                     MediaType selectedContentType,
                     Class<? extends HttpMessageConverter<?>> selectedConverterType,
                     ServerHttpRequest request,
                     ServerHttpResponse response);
}
