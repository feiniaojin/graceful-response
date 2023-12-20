package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.api.ValidationStatusCode;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
@Order(100)
public class ValidationExceptionAdvice {

    private final Logger logger = LoggerFactory.getLogger(ValidationExceptionAdvice.class);
    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    @Resource
    private ResponseFactory responseFactory;

    @Resource
    private GracefulResponseProperties gracefulResponseProperties;

    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public Response exceptionHandler(Exception e) throws Exception {

        if (e instanceof MethodArgumentNotValidException
                || e instanceof BindException) {
            ResponseStatus responseStatus = this.handleBindException((BindException) e);
            return responseFactory.newInstance(responseStatus);
        }

        if (e instanceof ConstraintViolationException) {
            ResponseStatus responseStatus = this.handleConstraintViolationException(e);
            return responseFactory.newInstance(responseStatus);
        }

        return responseFactory.newFailInstance();
    }

    //Controller方法的参数校验码
    //Controller方法>Controller类>DTO入参属性>DTO入参类>配置文件默认参数码>默认错误码
    private ResponseStatus handleBindException(BindException e) throws Exception {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String msg = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        String code;
        //Controller方法上的注解
        ValidationStatusCode validateStatusCode = this.findValidationStatusCodeInController();
        if (validateStatusCode != null) {
            code = validateStatusCode.code();
            return responseStatusFactory.newInstance(code, msg);
        }
        //属性校验上的注解，只会取第一个属性上的注解，因此要配置
        //hibernate.validator.fail_fast=true
        List<FieldError> fieldErrors = e.getFieldErrors();
        if (!CollectionUtils.isEmpty(fieldErrors)) {
            FieldError fieldError = fieldErrors.get(0);
            String fieldName = fieldError.getField();
            Object target = e.getTarget();
            Field field = null;
            Class<?> clazz = null;
            Object obj = target;
            if (fieldName.contains(".")) {
                String[] strings = fieldName.split("\\.");
                for (String fName : strings) {
                    clazz = obj.getClass();
                    field = obj.getClass().getDeclaredField(fName);
                    field.setAccessible(true);
                    obj = field.get(obj);
                }
            } else {
                clazz = target.getClass();
                field = target.getClass().getDeclaredField(fieldName);
            }

            ValidationStatusCode annotation = field.getAnnotation(ValidationStatusCode.class);
            //属性上找到注解
            if (annotation != null) {
                code = annotation.code();
                return responseStatusFactory.newInstance(code, msg);
            }
            //类上面找到注解
            annotation = clazz.getAnnotation(ValidationStatusCode.class);
            if (annotation != null) {
                code = annotation.code();
                return responseStatusFactory.newInstance(code, msg);
            }
        }
        //默认的参数异常码
        code = gracefulResponseProperties.getDefaultValidateErrorCode();
        if (StringUtils.hasLength(code)) {
            return responseStatusFactory.newInstance(code, msg);
        }
        //默认的异常码
        code = gracefulResponseProperties.getDefaultErrorCode();
        return responseStatusFactory.newInstance(code, msg);
    }

    /**
     * 当前Controller方法
     *
     * @return
     * @throws Exception
     */
    private Method currentControllerMethod() throws Exception {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
        HandlerExecutionChain handlerChain = requestMappingHandlerMapping.getHandler(sra.getRequest());
        assert handlerChain != null;
        HandlerMethod handler = (HandlerMethod) handlerChain.getHandler();
        return handler.getMethod();
    }

    private ResponseStatus handleConstraintViolationException(Exception e) throws Exception {

        ConstraintViolationException exception = (ConstraintViolationException) e;
        Set<ConstraintViolation<?>> violationSet = exception.getConstraintViolations();
        String msg = violationSet.stream().map(s -> s.getConstraintDescriptor().getMessageTemplate()).collect(Collectors.joining(";"));
        String code;
        ValidationStatusCode validationStatusCode = this.findValidationStatusCodeInController();
        if (validationStatusCode != null) {
            code = validationStatusCode.code();
            return responseStatusFactory.newInstance(code, msg);
        }
        //默认的参数异常码
        code = gracefulResponseProperties.getDefaultValidateErrorCode();
        if (StringUtils.hasLength(code)) {
            return responseStatusFactory.newInstance(code, msg);
        }
        //默认的异常码
        code = gracefulResponseProperties.getDefaultErrorCode();
        return responseStatusFactory.newInstance(code, msg);
    }

    /**
     * 找Controller中的ValidationStatusCode注解
     * 当前方法->当前Controller类
     *
     * @return
     * @throws Exception
     */
    private ValidationStatusCode findValidationStatusCodeInController() throws Exception {
        Method method = this.currentControllerMethod();
        //Controller方法上的注解
        ValidationStatusCode validateStatusCode = method.getAnnotation(ValidationStatusCode.class);
        //Controller类上的注解
        if (validateStatusCode == null) {
            validateStatusCode = method.getDeclaringClass().getAnnotation(ValidationStatusCode.class);
        }
        return validateStatusCode;
    }
}
