package com.feiniaojin.gracefulresponse.advice.lifecycle.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

/**
 * 接受异常的处理器
 *
 * @author qinyujie
 */
public interface BeforeResponseBodyAdviceProcess {
    /**
     * 处理前的回调
     *
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     */
    void call(Object body,
              MethodParameter returnType,
              MediaType selectedContentType,
              Class<? extends HttpMessageConverter<?>> selectedConverterType,
              ServerHttpRequest request,
              ServerHttpResponse response);
}
