package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseRejectException;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.RejectStrategy;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 如果异常不匹配，尝试寻找符合的异常处理
 *
 * @author qinyujie
 */
public class DefaultRejectStrategyImpl implements RejectStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRejectStrategyImpl.class);

    private Set<ExceptionHandlerModel> set = new CopyOnWriteArraySet<>();

    @Resource
    private AdviceSupport adviceSupport;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ReleaseExceptionHandlerExceptionResolver releaseExceptionHandlerExceptionResolver;

    @Override
    public Object call(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception exception) {

        if (adviceSupport.isMatchExcludeException(exception)) {
            LOGGER.debug("寻找是否有Graceful Response之外的异常处理器", exception);
            //寻找其他的异常处理Advice
            if (!set.isEmpty()) {
                Optional<HandlerMethod> handlerMethod = findHandlerMethod(exception);
                if (handlerMethod.isPresent()) {
                    return releaseExceptionHandlerExceptionResolver.doResolveHandlerMethodException(request, response, handlerMethod.get(), exception);
                }
            }
        }
        throw new GracefulResponseRejectException("异常不匹配，由ErrorController进行错误处理", exception);
    }

    @PostConstruct
    public void collectAllExceptionHandlers() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(ControllerAdvice.class);
        for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            Object object = entry.getValue();
            if (object instanceof AbstractControllerAdvice) {
                continue;
            }
            Method[] methods = object.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(ExceptionHandler.class)) {
                    LOGGER.debug("className={},methodName={}", object.getClass().getName(), method.getName());
                    set.add(new ExceptionHandlerModel(method, object));
                }
            }
        }
    }

    /**
     * 找到HandlerMethod异常对应的
     *
     * @param exception
     * @return
     */
    private Optional<HandlerMethod> findHandlerMethod(Exception exception) {
        Class<? extends Throwable> throwableClass = exception.getClass();
        Optional<ExceptionHandlerModel> modelOptional = set.stream().filter(model -> {
            Method method = model.getMethod();
            ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);
            Class<? extends Throwable>[] classes = exceptionHandler.value();
            return Arrays.stream(classes).anyMatch(clazz -> clazz.isAssignableFrom(throwableClass));
        }).findFirst();
        if (modelOptional.isEmpty()) {
            return Optional.empty();
        }
        ExceptionHandlerModel model = modelOptional.get();
        return Optional.of(new HandlerMethod(model.getBean(), model.getMethod()));
    }
}
