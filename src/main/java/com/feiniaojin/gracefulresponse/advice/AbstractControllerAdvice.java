package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.*;
import com.feiniaojin.gracefulresponse.data.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 抽象的异常处理基类
 *
 * @author qinyujie
 */
public abstract class AbstractControllerAdvice {

    /**
     * 执行处理之前的判断，只有所有的判断都生效，才会进行异常处理
     */
    private List<ControllerAdvicePredicate> predicates = new CopyOnWriteArrayList<>();

    private RejectStrategy rejectStrategy;

    private BeforeControllerAdviceProcess beforeControllerAdviceProcess;

    private AfterControllerAdviceProcess afterControllerAdviceProcess;

    private ControllerAdviceProcessor controllerAdviceProcessor;

    private ControllerAdviceHttpProcessor controllerAdviceHttpProcessor;

    public Object exceptionHandler(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerMethod handler, Exception exception) {
        //默认认为只要捕获到的，都要进行处理
        boolean hit = true;
        List<ControllerAdvicePredicate> pList = this.predicates;
        for (ControllerAdvicePredicate predicateBeforeHandle : pList) {
            if (!predicateBeforeHandle.shouldApplyTo(request, response, handler, exception)) {
                hit = false;
                break;
            }
        }

        //不需要处理，由RejectHandler决定该如何解决，默认为往后抛
        if (!hit) {
            return rejectStrategy.call(request, response, handler, exception);
        }

        //执行之前做一下进行回调，可以用于执行前的日志打印
        if (Objects.nonNull(beforeControllerAdviceProcess)) {
            beforeControllerAdviceProcess.call(request, response, handler, exception);
        }

        //处理异常，加工出来Response
        Response grResponse = controllerAdviceProcessor.process(request, response, handler, exception);

        //得到Response后的处理，可能需要打印日志
        if (Objects.nonNull(afterControllerAdviceProcess)) {
            afterControllerAdviceProcess.call(grResponse, exception);
        }

        //HTTP的处理收敛到这里，处理HTTP 状态码、Header
        return controllerAdviceHttpProcessor.process(grResponse, exception);
    }

    public List<ControllerAdvicePredicate> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<ControllerAdvicePredicate> predicates) {
        this.predicates = predicates;
    }

    public ControllerAdviceProcessor getControllerAdviceProcessor() {
        return controllerAdviceProcessor;
    }

    public void setControllerAdviceProcessor(ControllerAdviceProcessor controllerAdviceProcessor) {
        this.controllerAdviceProcessor = controllerAdviceProcessor;
    }

    public RejectStrategy getRejectStrategy() {
        return rejectStrategy;
    }

    public void setRejectStrategy(RejectStrategy rejectStrategy) {
        this.rejectStrategy = rejectStrategy;
    }

    public void setBeforeControllerAdviceProcess(BeforeControllerAdviceProcess beforeControllerAdviceProcess) {
        this.beforeControllerAdviceProcess = beforeControllerAdviceProcess;
    }

    public AfterControllerAdviceProcess getAfterControllerAdviceProcess() {
        return afterControllerAdviceProcess;
    }

    public void setAfterControllerAdviceProcess(AfterControllerAdviceProcess afterControllerAdviceProcess) {
        this.afterControllerAdviceProcess = afterControllerAdviceProcess;
    }

    public BeforeControllerAdviceProcess getBeforeControllerAdviceProcess() {
        return beforeControllerAdviceProcess;
    }

    public ControllerAdviceHttpProcessor getControllerAdviceHttpProcessor() {
        return controllerAdviceHttpProcessor;
    }

    public void setControllerAdviceHttpProcessor(ControllerAdviceHttpProcessor controllerAdviceHttpProcessor) {
        this.controllerAdviceHttpProcessor = controllerAdviceHttpProcessor;
    }
}
