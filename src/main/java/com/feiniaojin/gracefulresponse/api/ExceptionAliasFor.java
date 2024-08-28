package com.feiniaojin.gracefulresponse.api;


import com.feiniaojin.gracefulresponse.defaults.DefaultConstants;

import java.lang.annotation.*;

/**
 * 异常映射别名，把某个异常设置为外部异常的别名，以便自定义错误码和提示信息
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.3
 * @since 0.3
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExceptionAliasFor {

    /**
     * 异常对应的错误码.
     *
     * @return 异常对应的错误码
     */
    String code() default DefaultConstants.DEFAULT_ERROR_CODE;

    /**
     * 异常信息.
     *
     * @return 异常对应的提示信息
     */
    String msg() default DefaultConstants.DEFAULT_ERROR_MSG;

    /**
     * 作为某些异常的别名
     *
     * @return
     */
    Class<? extends Throwable>[] aliasFor();

    int httpStatusCode() default -1;
}
