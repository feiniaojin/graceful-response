package com.feiniaojin.gracefulresponse.advice.lifecycle.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * 处理之前需要判断是否需要处理
 *
 * @author qinyujie
 */
public interface ResponseBodyAdvicePredicate {

    /**
     * 判断是否需要处理
     *
     * @param methodParameter
     * @param clazz
     * @return
     */
    default boolean shouldApplyTo(MethodParameter methodParameter,
                         Class<? extends HttpMessageConverter<?>> clazz) {
        return true;
    }
}
