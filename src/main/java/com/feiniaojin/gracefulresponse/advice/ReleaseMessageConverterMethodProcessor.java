package com.feiniaojin.gracefulresponse.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;

import java.util.List;

/**
 * 被拒绝的异常继续分发给组件外进行处理，MessageConverterMethodProcessor
 *
 * @author qinyujie
 */
public class ReleaseMessageConverterMethodProcessor extends HttpEntityMethodProcessor {

    public ReleaseMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public ReleaseMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager) {
        super(converters, manager);
    }

    public ReleaseMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters, List<Object> requestResponseBodyAdvice) {
        super(converters, requestResponseBodyAdvice);
    }

    public ReleaseMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager, List<Object> requestResponseBodyAdvice) {
        super(converters, manager, requestResponseBodyAdvice);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return Boolean.TRUE;
    }

    @Override
    protected ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        ServletServerHttpResponse outputMessage = super.createOutputMessage(webRequest);
        outputMessage.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return outputMessage;
    }

    @Override
    protected ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        ServletServerHttpRequest inputMessage = super.createInputMessage(webRequest);
        inputMessage.getHeaders().add("Accept", "application/json;charset=UTF-8");
        return inputMessage;
    }

}
