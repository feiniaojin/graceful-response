package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseDataException;
import com.feiniaojin.gracefulresponse.GracefulResponseException;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdviceHttpProcessor;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdvicePredicate;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdviceProcessor;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

/**
 * 处理GracefulResponseDataException的异常
 *
 * @author qinyujie
 */
@Order(190)
@ControllerAdvice
public class DataExceptionAdvice extends AbstractControllerAdvice
        implements ControllerAdvicePredicate, ControllerAdviceProcessor,
        ControllerAdviceHttpProcessor {

    @Resource
    private ResponseFactory responseFactory;

    @Resource
    private GracefulResponseProperties properties;

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    @Override
    public Response process(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception exception) {
        if (exception instanceof GracefulResponseDataException dataException) {
            ResponseStatus statusLine = fromGracefulResponseExceptionInstance(dataException);
            return responseFactory.newInstance(statusLine, dataException.getData());
        }
        throw new UnsupportedOperationException();
    }

    private ResponseStatus fromGracefulResponseExceptionInstance(GracefulResponseException exception) {
        String code = exception.getCode();
        if (code == null) {
            code = properties.getDefaultErrorCode();
        }
        return responseStatusFactory.newInstance(code, exception.getMsg());
    }

    @Override
    @ExceptionHandler(value = GracefulResponseDataException.class)
    public Object exceptionHandler(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerMethod handler, Exception exception) {
        return super.exceptionHandler(request, response, handler, exception);
    }

    @Override
    public boolean shouldApplyTo(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception exception) {
        return exception instanceof GracefulResponseDataException;
    }
}


