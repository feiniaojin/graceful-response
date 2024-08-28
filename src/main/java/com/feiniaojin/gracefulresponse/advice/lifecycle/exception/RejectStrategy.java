package com.feiniaojin.gracefulresponse.advice.lifecycle.exception;

import com.feiniaojin.gracefulresponse.data.Response;
import org.springframework.http.ResponseEntity;

/**
 * 拒绝策略，如果异常不匹配，拒绝处理的实现，默认为将异常抛出
 *
 * @author qinyujie
 */
public interface RejectStrategy {
    /**
     *
     * @param throwable
     * @return
     */
    ResponseEntity<Response> call(Throwable throwable);
}
