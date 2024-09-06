package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.ExceptionAliasRegister;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdviceHttpProcessor;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdvicePredicate;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdviceProcessor;
import com.feiniaojin.gracefulresponse.api.ExceptionAliasFor;
import com.feiniaojin.gracefulresponse.api.ExceptionMapper;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.data.ExceptionAliasConfig;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;

import java.util.Map;
import java.util.Objects;

/**
 * 默认的全局异常处理
 *
 * @author qinyujie
 */
@Order(200)
@ControllerAdvice
public class DefaultGlobalExceptionAdvice extends AbstractControllerAdvice implements ControllerAdvicePredicate,
        ControllerAdviceProcessor, ControllerAdviceHttpProcessor {

    @Resource
    private ExceptionAliasRegister exceptionAliasRegister;

    @Resource
    private ResponseFactory responseFactory;

    @Resource
    private GracefulResponseProperties properties;

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    @Resource
    private AdviceSupport adviceSupport;

    @Override
    public Response process(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception exception) {
        ResponseStatus statusLine = fromExceptionInstance(exception);
        return responseFactory.newInstance(statusLine);
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

        Map<Class<?>, ExceptionAliasConfig> exceptionAliasConfigMap = properties.getExceptionAliasConfigMap();
        if (!CollectionUtils.isEmpty(exceptionAliasConfigMap)) {
            ExceptionAliasConfig exceptionAliasConfig = exceptionAliasConfigMap.get(clazz);
            if (exceptionAliasConfig != null) {
                return responseStatusFactory.newInstance(exceptionAliasConfig.getCode(),
                        exceptionAliasConfig.getMsg());
            }
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
        if (Boolean.TRUE.equals(properties.getOriginExceptionUsingDetailMessage())) {
            String throwableMessage = throwable.getMessage();
            if (throwableMessage != null) {
                defaultError.setMsg(throwableMessage);
            }
        }
        return defaultError;
    }

    @Override
    @ExceptionHandler(value = Throwable.class)
    public Object exceptionHandler(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerMethod handler, Exception exception) {
        return super.exceptionHandler(request, response, handler, exception);
    }

    @Override
    public boolean shouldApplyTo(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception exception) {

        if (adviceSupport.isMatchExcludeException(exception)) {
            //符合放行规则，在放行之前记录一下，方便ResponseBodyAdvice处理
            RequestContextHolder.currentRequestAttributes()
                    .setAttribute(ReleaseExceptionHandlerExceptionResolver.RELEASE_EXCEPTION_KEY,
                            exception, RequestAttributes.SCOPE_REQUEST);
            //放行不处理，false
            return false;
        }
        return true;
    }

    @Override
    public ResponseEntity<Response> process(Response response, Throwable throwable) {

        //HTTP ResponseStatus的处理
        org.springframework.web.bind.annotation.ResponseStatus httpResponseStatus = AnnotatedElementUtils.findMergedAnnotation(throwable.getClass(), org.springframework.web.bind.annotation.ResponseStatus.class);

        HttpStatusCode httpStatusCode;
        if (Objects.nonNull(httpResponseStatus)) {
            //取ResponseStatus注解中的状态码
            httpStatusCode = httpResponseStatus.value();
        } else {
            //优先取配置文件的异常别名
            Map<Class<?>, ExceptionAliasConfig> exceptionAliasConfigMap = properties.getExceptionAliasConfigMap();
            Class<? extends Throwable> aClass = throwable.getClass();
            if (!CollectionUtils.isEmpty(exceptionAliasConfigMap)
                    && Objects.nonNull(exceptionAliasConfigMap.get(aClass))
                    && Objects.nonNull(exceptionAliasConfigMap.get(aClass).getHttpStatusCode())) {
                httpStatusCode = HttpStatusCode.valueOf(exceptionAliasConfigMap.get(aClass).getHttpStatusCode());
            } else {
                //取代码中注册的异常别名
                ExceptionAliasFor exceptionAliasFor = exceptionAliasRegister.getExceptionAliasFor(throwable.getClass());
                if (exceptionAliasFor != null
                        && exceptionAliasFor.httpStatusCode() > 0) {
                    httpStatusCode = HttpStatusCode.valueOf(exceptionAliasFor.httpStatusCode());
                } else {
                    //取用户配置的默认错误码
                    Integer defaultHttpStatusCodeOnError = properties.getDefaultHttpStatusCodeOnError();
                    httpStatusCode = HttpStatusCode.valueOf(defaultHttpStatusCodeOnError);
                }
            }
        }
        return new ResponseEntity<>(response, httpStatusCode);
    }
}


