package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.ExceptionAliasRegister;
import com.feiniaojin.gracefulresponse.GracefulResponseDataException;
import com.feiniaojin.gracefulresponse.GracefulResponseException;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ExceptionAliasFor;
import com.feiniaojin.gracefulresponse.api.ExceptionMapper;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
@ControllerAdvice
@Order(200)
public class GrGlobalExceptionAdvice implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(GrGlobalExceptionAdvice.class);

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
            statusLine = fromExceptionInstance(throwable);
        }
        return responseFactory.newInstance(statusLine);
    }

    /**
     * 带数据的异常处理逻辑.
     *
     * @param throwable 业务逻辑抛出的异常
     * @return 统一返回包装后的结果
     */
    @ExceptionHandler({GracefulResponseDataException.class})
    @ResponseBody
    public Response exceptionHandler(GracefulResponseDataException throwable) {
        if (gracefulResponseProperties.isPrintExceptionInGlobalAdvice()) {
            logger.error("Graceful Response:GlobalExceptionAdvice捕获到异常,message=[{}]", throwable.getMessage(), throwable);
        }
        ResponseStatus statusLine = fromGracefulResponseExceptionInstance(throwable);
        return responseFactory.newInstance(statusLine,throwable.getData());
    }

    private ResponseStatus fromGracefulResponseExceptionInstance(GracefulResponseException exception) {
        String code = exception.getCode();
        if (code == null) {
            code = properties.getDefaultErrorCode();
        }
        return responseStatusFactory.newInstance(code,
                exception.getMsg());
    }

    private ResponseStatus fromExceptionInstance(Throwable throwable) {

        Class<? extends Throwable> clazz = throwable.getClass();

        ExceptionMapper exceptionMapper = clazz.getAnnotation(ExceptionMapper.class);

        //1.有@ExceptionMapper注解，直接设置结果的状态
        if (exceptionMapper != null) {
            boolean msgReplaceable = exceptionMapper.msgReplaceable();
            //异常提示可替换+抛出来的异常有自定义的异常信息
            if (msgReplaceable) {
                String throwableMessage = throwable.getMessage();
                if (throwableMessage != null) {
                    return responseStatusFactory.newInstance(exceptionMapper.code(), throwableMessage);
                }
            }
            return responseStatusFactory.newInstance(exceptionMapper.code(),
                    exceptionMapper.msg());
        }

        //2.有@ExceptionAliasFor异常别名注解，获取已注册的别名信息
        if (exceptionAliasRegister != null) {
            ExceptionAliasFor exceptionAliasFor = exceptionAliasRegister.getExceptionAliasFor(clazz);
            if (exceptionAliasFor != null) {
                return responseStatusFactory.newInstance(exceptionAliasFor.code(),
                        exceptionAliasFor.msg());
            }
        }
        ResponseStatus defaultError = responseStatusFactory.defaultError();

        //3. 原生异常+originExceptionUsingDetailMessage=true
        //如果有自定义的异常信息，原生异常将直接使用异常信息进行返回，不再返回默认错误提示
        if (properties.getOriginExceptionUsingDetailMessage()) {
            String throwableMessage = throwable.getMessage();
            if (throwableMessage != null) {
                defaultError.setMsg(throwableMessage);
            }
        }
        return defaultError;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.exceptionAliasRegister = applicationContext.getBean(ExceptionAliasRegister.class);
    }
}
