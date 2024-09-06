package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.BeforeControllerAdviceProcess;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

/**
 * 默认的处理前回调
 *
 * @author qinyujie
 */
public class DefaultBeforeControllerAdviceProcessImpl implements BeforeControllerAdviceProcess {

    private final Logger logger = LoggerFactory.getLogger(DefaultBeforeControllerAdviceProcessImpl.class);

    @Resource
    private GracefulResponseProperties properties;

    @Override
    public void call(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        if (properties.isPrintExceptionInGlobalAdvice()) {
            logger.error("Graceful Response:捕获到异常,message=[{}]", ex.getMessage(), ex);
        }
    }
}
