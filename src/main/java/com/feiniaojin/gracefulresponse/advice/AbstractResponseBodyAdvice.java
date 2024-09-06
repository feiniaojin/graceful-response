package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.RejectStrategy;
import com.feiniaojin.gracefulresponse.advice.lifecycle.response.AfterResponseBodyAdviceProcess;
import com.feiniaojin.gracefulresponse.advice.lifecycle.response.BeforeResponseBodyAdviceProcess;
import com.feiniaojin.gracefulresponse.advice.lifecycle.response.ResponseBodyAdvicePredicate;
import com.feiniaojin.gracefulresponse.advice.lifecycle.response.ResponseBodyAdviceProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 抽象的异常处理基类
 *
 * @author qinyujie
 */
public abstract class AbstractResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 执行处理之前的判断，只有所有的判断都生效，才会进行异常处理
     */
    private List<ResponseBodyAdvicePredicate> predicates = new CopyOnWriteArrayList<>();

    private RejectStrategy rejectStrategy = new DefaultRejectStrategyImpl();

    private BeforeResponseBodyAdviceProcess beforeResponseBodyAdviceProcess;

    private AfterResponseBodyAdviceProcess afterResponseBodyAdviceProcess;

    private ResponseBodyAdviceProcessor responseBodyAdviceProcessor;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //默认认为只要捕获到的，都要进行处理
        boolean hit = true;
        List<ResponseBodyAdvicePredicate> pList = this.predicates;
        for (ResponseBodyAdvicePredicate predicateBeforeHandle : pList) {
            if (!predicateBeforeHandle.shouldApplyTo(returnType, converterType)) {
                hit = false;
                break;
            }
        }
        return hit;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        //执行之前做一下进行回调，可以用于执行前的日志打印
        if (Objects.nonNull(beforeResponseBodyAdviceProcess)) {
            beforeResponseBodyAdviceProcess.call(body,
                    returnType, selectedContentType, selectedConverterType, request, response);
        }

        //处理
        body = responseBodyAdviceProcessor.process(body,
                returnType,
                selectedContentType, selectedConverterType, request, response);

        if (Objects.nonNull(afterResponseBodyAdviceProcess)) {
            afterResponseBodyAdviceProcess.call(body,
                    returnType, selectedContentType, selectedConverterType, request, response);
        }
        return body;
    }

    public List<ResponseBodyAdvicePredicate> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<ResponseBodyAdvicePredicate> predicates) {
        this.predicates = predicates;
    }

    public RejectStrategy getRejectStrategy() {
        return rejectStrategy;
    }

    public void setRejectStrategy(RejectStrategy rejectStrategy) {
        this.rejectStrategy = rejectStrategy;
    }

    public BeforeResponseBodyAdviceProcess getBeforeResponseBodyAdviceProcess() {
        return beforeResponseBodyAdviceProcess;
    }

    public void setBeforeResponseBodyAdviceProcess(BeforeResponseBodyAdviceProcess beforeResponseBodyAdviceProcess) {
        this.beforeResponseBodyAdviceProcess = beforeResponseBodyAdviceProcess;
    }

    public AfterResponseBodyAdviceProcess getAfterResponseBodyAdviceProcess() {
        return afterResponseBodyAdviceProcess;
    }

    public void setAfterResponseBodyAdviceProcess(AfterResponseBodyAdviceProcess afterResponseBodyAdviceProcess) {
        this.afterResponseBodyAdviceProcess = afterResponseBodyAdviceProcess;
    }

    public ResponseBodyAdviceProcessor getResponseBodyAdviceProcessor() {
        return responseBodyAdviceProcessor;
    }

    public void setResponseBodyAdviceProcessor(ResponseBodyAdviceProcessor responseBodyAdviceProcessor) {
        this.responseBodyAdviceProcessor = responseBodyAdviceProcessor;
    }
}
