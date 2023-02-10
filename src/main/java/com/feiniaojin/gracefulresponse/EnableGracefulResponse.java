package com.feiniaojin.gracefulresponse;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * 注解启动全局结果处理的入口.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AutoConfig.class)
public @interface EnableGracefulResponse {
}
