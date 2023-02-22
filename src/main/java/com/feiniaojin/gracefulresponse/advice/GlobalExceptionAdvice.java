package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.ExceptionAliasRegister;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.*;
import com.feiniaojin.gracefulresponse.data.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 全局异常处理.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
@ControllerAdvice
public class GlobalExceptionAdvice implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    @Resource
    private ResponseFactory responseFactory;

    private ExceptionAliasRegister exceptionAliasRegister;

    @Resource
    private GracefulResponseProperties gracefulResponseProperties;

    /**
     * 异常处理逻辑.
     *
     * @param throwable 业务逻辑抛出的异常
     * @return 统一返回包装后的结果
     */
    @ExceptionHandler({Throwable.class})
    @ResponseBody
    public Response exceptionHandler(Throwable throwable) {
        if (gracefulResponseProperties.isPrintExceptionInGlobalAdvice()) {
            logger.error("GlobalExceptionAdvice捕获到异常", throwable);
        }

        return generateResponse(throwable);
    }

    private Response generateResponse(Throwable throwable) {

        Class<? extends Throwable> throwableClass = throwable.getClass();
        ExceptionMapper mapper = throwableClass.getAnnotation(ExceptionMapper.class);

        if (mapper != null) {
            if (mapper.renderer() != ExceptionRenderer.class) {
                return renderResponse(mapper.renderer(), throwable);
            } else {
                return responseFactory.newInstance(responseStatusFactory.newInstance(mapper.code(), mapper.msg()));
            }
        }

        //获取已注册的别名
        if (exceptionAliasRegister != null) {
            ExceptionAliasFor alias = exceptionAliasRegister.getExceptionAliasFor(throwableClass);
            if (alias != null) {
                if (alias.renderer() != ExceptionRenderer.class) {
                    return renderResponse(alias.renderer(), throwable);
                } else {
                    return responseFactory.newInstance(responseStatusFactory.newInstance(alias.code(), alias.msg()));
                }
            }
        }

        return responseFactory.newInstance(responseStatusFactory.defaultFail());
    }

    private Response renderResponse(Class<? extends ExceptionRenderer> rendererClass, Throwable throwable) {
        try {
            ExceptionRenderer renderer = rendererClass.newInstance();
            return renderer.render(throwable, responseFactory, responseStatusFactory);
        } catch (Exception e) {
            return responseFactory.newFailInstance();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.exceptionAliasRegister = applicationContext.getBean(ExceptionAliasRegister.class);
    }
}
