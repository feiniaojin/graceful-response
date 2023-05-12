package com.feiniaojin.gracefulresponse.api;

import com.feiniaojin.gracefulresponse.defaults.DefaultConstants;

import java.lang.annotation.*;

/**
 * 指定参数校验的异常码
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
}
