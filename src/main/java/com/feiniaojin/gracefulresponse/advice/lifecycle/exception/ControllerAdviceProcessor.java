package com.feiniaojin.gracefulresponse.advice.lifecycle.exception;

import com.feiniaojin.gracefulresponse.data.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

/**
 * 异常处理器
 *
 * @author qinyujie
 */
public interface ControllerAdviceProcessor {

    /**
     * 异常处理
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return
     */
    Response process(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex);
}
