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
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
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

    private static ExpressionParser parser = new SpelExpressionParser();


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
    //Controller方法>Controller类>DTO入参属性>DTO入参属性所在类>DTO入参根类>配置文件默认参数码>默认错误码
    //这个取值顺序的逻辑是按照个性化程度由高到底排序的
    private ResponseStatus handleBindException(BindException e) throws Exception {
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String msg = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        String code;
        //Controller方法和类上的注解
        ValidationStatusCode validateStatusCode = this.findValidationStatusCodeInController();
        if (validateStatusCode != null) {
            code = validateStatusCode.code();
            return responseStatusFactory.newInstance(code, msg);
        }

        //DTO入参属性
        List<FieldError> fieldErrors = e.getFieldErrors();
        FieldError fieldError = fieldErrors.get(0);
        String fieldName = fieldError.getField();
        Field field = null;
        Class<?> clazz = null;
        Object target = bindingResult.getTarget();
        //不包含.，说明直接是根路径
        if (!fieldName.contains(".")) {
            clazz = target.getClass();
        } else {
            String fieldParentPath = fieldParentPath(fieldName);
            fieldName = fieldSimpleName(fieldName);
            Expression expression = parser.parseExpression(fieldParentPath);
            clazz = expression.getValue(target).getClass();
        }
        field = clazz.getDeclaredField(fieldName);

        ValidationStatusCode annotation = field.getAnnotation(ValidationStatusCode.class);
        //属性上找到注解
        if (annotation != null) {
            code = annotation.code();
            return responseStatusFactory.newInstance(code, msg);
        }
        //属性所在类上面找到注解
        annotation = clazz.getAnnotation(ValidationStatusCode.class);
        if (annotation != null) {
            code = annotation.code();
            return responseStatusFactory.newInstance(code, msg);
        }

        //根类上找注解
        if (target.getClass() != clazz) {
            annotation = target.getClass().getAnnotation(ValidationStatusCode.class);
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

    private String fieldSimpleName(String fieldName) {
        int lastIndex = fieldName.lastIndexOf(".");
        StringBuilder stringBuilder = new StringBuilder();
        int length = fieldName.length();
        for (int i = lastIndex + 1; i < length; i++) {
            stringBuilder.append(fieldName.charAt(i));
        }
        return stringBuilder.toString();
    }

    /**
     * 属性父类路径
     *
     * @param fieldName
     * @return
     */
    private String fieldParentPath(String fieldName) {
        int lastIndex = fieldName.lastIndexOf(".");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lastIndex; i++) {
            stringBuilder.append(fieldName.charAt(i));
        }
        return stringBuilder.toString();
    }

    private String leafPropertyFieldName(String fieldName) {
        int lastIndexOf = fieldName.lastIndexOf('.');
        if (lastIndexOf == -1) {
            return fieldName;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int len = fieldName.length();
        for (int i = lastIndexOf + 1; i < len; i++) {
            stringBuilder.append(fieldName.charAt(i));
        }
        return stringBuilder.toString();
    }

    /**
     * 适配数组属性，包括数组、集合
     *
     * @param fieldName
     * @return
     */
    private String adapterArrayProperty(String fieldName) {
        //最后一个字符不是]，则不是数组属性
        if (fieldName.lastIndexOf(']') == -1) {
            return fieldName;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int len = fieldName.length();
        char c;
        for (int i = 0; i < len; i++) {
            c = fieldName.charAt(i);
            if (c == '[') {
                break;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
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
