package com.feiniaojin.gracefulresponse.api;

import com.feiniaojin.gracefulresponse.defaults.DefaultConstants;

import java.lang.annotation.*;

/**
 * 指定参数校验的异常码
 *
 * @author qinyujie
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ValidationStatusCode {

    /**
     * 异常对应的错误码.
     *
     * @return 异常对应的错误码
     */
    String code() default DefaultConstants.DEFAULT_ERROR_CODE;

    /**
     * 默认给了-1，代表不需要设置HTTP状态码
     *
     * @return HTTP状态码
     * @since 5.0.0 指定参数校验异常时的HTTP状态码
     */
    int httpStatusCode() default -1;
}
