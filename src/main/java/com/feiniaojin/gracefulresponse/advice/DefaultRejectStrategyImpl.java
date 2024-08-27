package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseException;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.RejectStrategy;
import com.feiniaojin.gracefulresponse.data.Response;
import org.springframework.http.ResponseEntity;

/**
 * 如果异常不匹配，拒绝处理的实现，默认为将异常抛出
 *
 * @author qinyujie
 */
public class DefaultRejectStrategyImpl implements RejectStrategy {

    @Override
    public ResponseEntity<Response> call(Throwable throwable) {
        throw new GracefulResponseException("异常不匹配，抛出由后续处理器处理", throwable);
    }
}
