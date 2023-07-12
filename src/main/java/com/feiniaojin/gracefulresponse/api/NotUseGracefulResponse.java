package com.feiniaojin.gracefulresponse.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用此注解直接返回controller属性, 不封装
 *
 * @author lihao3
 * @date 2023/7/12 9:57
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NotUseGracefulResponse {
}
