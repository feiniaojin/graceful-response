package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.ExceptionAliasRegister;
import com.feiniaojin.gracefulresponse.GracefulResponseException;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ExceptionAliasFor;
import com.feiniaojin.gracefulresponse.api.ExceptionMapper;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
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
@Order(200)
public class GlobalExceptionAdvice implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    @Resource
    private ResponseFactory responseFactory;

    private ExceptionAliasRegister exceptionAliasRegister;

    @Resource
    private GracefulResponseProperties gracefulResponseProperties;

    @Resource
    private GracefulResponseProperties properties;

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
            logger.error("Graceful Response:GlobalExceptionAdvice捕获到异常,message=[{}]", throwable.getMessage(), throwable);
        }
        ResponseStatus statusLine;
        if (throwable instanceof GracefulResponseException) {
            statusLine = fromGracefulResponseExceptionInstance((GracefulResponseException) throwable);
        } else {
            //校验异常转自定义异常
            statusLine = fromExceptionClass(throwable.getClass());
        }
        return responseFactory.newInstance(statusLine);
    }

    private ResponseStatus fromGracefulResponseExceptionInstance(GracefulResponseException exception) {
        return responseStatusFactory.newInstance(exception.getCode(),
                exception.getMsg());
    }

    private ResponseStatus fromExceptionClass(Class<? extends Throwable> clazz) {

        ExceptionMapper exceptionMapper = clazz.getAnnotation(ExceptionMapper.class);

        if (exceptionMapper != null) {
            return responseStatusFactory.newInstance(exceptionMapper.code(),
                    exceptionMapper.msg());
        }

        //获取已注册的别名
        if (exceptionAliasRegister != null) {
            ExceptionAliasFor exceptionAliasFor = exceptionAliasRegister.getExceptionAliasFor(clazz);
            if (exceptionAliasFor != null) {
                return responseStatusFactory.newInstance(exceptionAliasFor.code(),
                        exceptionAliasFor.msg());
            }
        }

        return responseStatusFactory.defaultError();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.exceptionAliasRegister = applicationContext.getBean(ExceptionAliasRegister.class);
    }
}
