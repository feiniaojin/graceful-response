package com.feiniaojin.gracefulresponse.advice.lifecycle.exception;

import com.feiniaojin.gracefulresponse.data.Response;
import org.springframework.http.ResponseEntity;

/**
 * 计算异常对应的HTTP处理器
 *
 * @author qinyujie
 */
public interface ControllerAdviceHttpProcessor {

    default ResponseEntity<Response> process(Response response, Throwable throwable) {
        return ResponseEntity.ofNullable(response);
    }
}
