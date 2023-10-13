package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.api.ValidationStatusCode;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
@Order(100)
public class ValidationExceptionAdvice {

    @Resource
    private RequestMappingHandlerMapping mapping;

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    @Resource
    private ResponseFactory responseFactory;

    @Resource
    private GracefulResponseProperties gracefulResponseProperties;

    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public Response exceptionHandler(Exception e) throws Exception {

        if (e instanceof MethodArgumentNotValidException) {
            ResponseStatus responseStatus = this.fromMethodArgumentNotValidException(e);
            return responseFactory.newInstance(responseStatus);
        }

        if (e instanceof BindException) {
            ResponseStatus responseStatus = this.fromBindException(e);
            return responseFactory.newInstance(responseStatus);
        }

        if (e instanceof ConstraintViolationException) {
            ResponseStatus responseStatus = this.fromConstraintViolationException(e);
            return responseFactory.newInstance(responseStatus);
        }

        return responseFactory.newFailInstance();
    }

    private ResponseStatus fromMethodArgumentNotValidException(Exception e) throws Exception {
        MethodArgumentNotValidException me = (MethodArgumentNotValidException) e;
        List<ObjectError> allErrors = me.getBindingResult().getAllErrors();
        String msg = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        String code = this.determineErrorCode();
        return responseStatusFactory.newInstance(code, msg);
    }

    private String determineErrorCode() throws Exception {
        Method method = this.currentControllerMethod();
        ValidationStatusCode validateStatusCode = method.getAnnotation(ValidationStatusCode.class);
        if (validateStatusCode == null) {
            validateStatusCode = method.getDeclaringClass().getAnnotation(ValidationStatusCode.class);
        }
        if (validateStatusCode != null) {
            return validateStatusCode.code();
        }
        return gracefulResponseProperties.getDefaultValidateErrorCode();
    }

    private Method currentControllerMethod() throws Exception {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
        HandlerExecutionChain handlerChain = mapping.getHandler(sra.getRequest());
        assert handlerChain != null;
        HandlerMethod handler = (HandlerMethod) handlerChain.getHandler();
        return handler.getMethod();
    }

    private ResponseStatus fromConstraintViolationException(Exception e) throws Exception {

        ConstraintViolationException exception = (ConstraintViolationException) e;
        Set<ConstraintViolation<?>> violationSet = exception.getConstraintViolations();
        String msg = violationSet.stream().map(s -> s.getConstraintDescriptor().getMessageTemplate()).collect(Collectors.joining(";"));
        String code = this.determineErrorCode();
        return responseStatusFactory.newInstance(code, msg);
    }

    private ResponseStatus fromBindException(Exception e) throws NoSuchFieldException {

        String code;

        BindException bindException = (BindException) e;
        FieldError fieldError = bindException.getFieldError();
        assert fieldError != null;
        String fieldName = fieldError.getField();
        Object target = bindException.getTarget();
        assert target != null;
        Field declaredField = target.getClass().getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        ValidationStatusCode annotation = declaredField.getAnnotation(ValidationStatusCode.class);
        declaredField.setAccessible(false);

        //属性上找不到注解，尝试获取类上的注解
        if (annotation == null) {
            annotation = target.getClass().getAnnotation(ValidationStatusCode.class);
        }

        if (annotation != null) {
            code = annotation.code();
        } else {
            code = gracefulResponseProperties.getDefaultValidateErrorCode();
        }

        String msg = bindException.getAllErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(";"));

        return responseStatusFactory.newInstance(code, msg);
    }

}
