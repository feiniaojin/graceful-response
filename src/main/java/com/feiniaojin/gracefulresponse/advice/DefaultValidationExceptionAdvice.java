package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseException;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdviceHttpProcessor;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdvicePredicate;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdviceProcessor;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.api.ValidationStatusCode;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认的全局异常处理
 *
 * @author qinyujie
 */
@Order(100)
@ControllerAdvice
public class DefaultValidationExceptionAdvice extends AbstractControllerAdvice
        implements ControllerAdvicePredicate, ControllerAdviceProcessor,
        ControllerAdviceHttpProcessor {

    private final Logger logger = LoggerFactory.getLogger(DefaultValidationExceptionAdvice.class);

    private static final String VALIDATION_STATUS_CODE = "ValidationStatusCode";

    private static final String PACKAGE_SEP = ".";

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Resource
    private ResponseFactory responseFactory;

    @Resource
    private GracefulResponseProperties properties;

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    @Override
    public Response process(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        ResponseStatus responseStatus = null;
        if (ex instanceof BindException exception) {
            responseStatus = this.handleBindException(exception);
        } else if (ex instanceof ConstraintViolationException exception) {
            responseStatus = this.handleConstraintViolationException(exception);
        }
        Response grRresponse;
        if (Objects.nonNull(responseStatus)) {
            grRresponse = responseFactory.newInstance(responseStatus);
        } else {
            grRresponse = responseFactory.newFailInstance();
        }
        return grRresponse;
    }

    @Override
    @ExceptionHandler(value = {BindException.class,
            ValidationException.class,
            MethodArgumentNotValidException.class})
    public Object exceptionHandler(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerMethod handler, Exception exception) {
        return super.exceptionHandler(request, response, handler, exception);
    }

    /**
     * Controller方法的参数校验码
     * Controller方法>Controller类>DTO入参属性>DTO入参属性所在类>DTO入参根类>配置文件默认参数码>默认错误码
     * 这个取值顺序的逻辑是按照个性化程度由高到底排序的
     *
     * @param e
     * @return
     */
    private ResponseStatus handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String msg = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        String code;

        ValidationStatusCode validateStatusCode = this.findValidationStatusCode(e);
        if (Objects.nonNull(validateStatusCode)) {
            //找这个注解实在太费劲了，保存起来后面再用吧
            RequestContextHolder.currentRequestAttributes().setAttribute(VALIDATION_STATUS_CODE,
                    validateStatusCode,
                    RequestAttributes.SCOPE_REQUEST);

            //取出预定义的参数校验码
            code = validateStatusCode.code();
            if (StringUtils.hasLength(code)) {
                return responseStatusFactory.newInstance(code, msg);
            }
        }
        //默认的参数异常码
        code = properties.getDefaultValidateErrorCode();
        if (StringUtils.hasLength(code)) {
            return responseStatusFactory.newInstance(code, msg);
        }
        //默认的异常码
        code = properties.getDefaultErrorCode();
        return responseStatusFactory.newInstance(code, msg);
    }

    /**
     * 找到ValidationStatusCode注解
     *
     * @param e
     * @return
     */
    private ValidationStatusCode findValidationStatusCode(BindException e) {

        ValidationStatusCode validateStatusCode = this.findValidationStatusCodeInController();

        if (Objects.nonNull(validateStatusCode)) {
            return validateStatusCode;
        }

        BindingResult bindingResult = e.getBindingResult();

        //DTO入参属性
        List<FieldError> fieldErrors = e.getFieldErrors();
        if (fieldErrors.isEmpty()) {
            return null;
        }

        FieldError fieldError = fieldErrors.get(0);
        String fieldName = fieldError.getField();
        Field field = null;
        Class<?> clazz = null;
        Object target = bindingResult.getTarget();
        if (Objects.isNull(target)) {
            return null;
        }
        //不包含.，说明直接是根路径
        if (!fieldName.contains(PACKAGE_SEP)) {
            clazz = target.getClass();
        } else {
            String fieldParentPath = fieldParentPath(fieldName);
            fieldName = fieldSimpleName(fieldName);
            Expression expression = PARSER.parseExpression(fieldParentPath);
            Object object = expression.getValue(target);
            if (Objects.isNull(object)) {
                throw new GracefulResponseException();
            }
            clazz = object.getClass();
        }
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (Exception exception) {
            logger.error("无法根据属性名找到对应的属性,fieldName={}", fieldName);
            throw new GracefulResponseException("获得校验不通过的属性失败,fieldName={}", fieldName, exception);
        }

        validateStatusCode = field.getAnnotation(ValidationStatusCode.class);
        //属性上找到注解
        if (Objects.nonNull(validateStatusCode)) {
            return validateStatusCode;
        }

        //属性所在类上面找到注解
        validateStatusCode = clazz.getAnnotation(ValidationStatusCode.class);
        if (Objects.nonNull(validateStatusCode)) {
            return validateStatusCode;
        }

        //根类上找注解
        if (target.getClass() != clazz) {
            validateStatusCode = target.getClass().getAnnotation(ValidationStatusCode.class);
            if (Objects.nonNull(validateStatusCode)) {
                return validateStatusCode;
            }
        }

        return null;
    }

    private String fieldSimpleName(String fieldName) {
        int lastIndex = fieldName.lastIndexOf(".");
        return fieldName.substring(lastIndex + 1);
    }

    /**
     * 属性父类路径
     *
     * @param fieldName
     * @return
     */
    private String fieldParentPath(String fieldName) {
        int lastIndex = fieldName.lastIndexOf(".");
        return fieldName.substring(0, lastIndex);
    }

    /**
     * 当前Controller方法
     *
     * @return
     * @throws Exception
     */
    private Method currentControllerMethod() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
            HandlerExecutionChain handlerChain = requestMappingHandlerMapping.getHandler(sra.getRequest());
            assert handlerChain != null;
            HandlerMethod handler = (HandlerMethod) handlerChain.getHandler();
            return handler.getMethod();
        } catch (Exception e) {
            throw new GracefulResponseException("获取当前Controller方法失败", e);
        }
    }

    private ResponseStatus handleConstraintViolationException(Exception e) {

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
        code = properties.getDefaultValidateErrorCode();
        if (StringUtils.hasLength(code)) {
            return responseStatusFactory.newInstance(code, msg);
        }
        //默认的异常码
        code = properties.getDefaultErrorCode();
        return responseStatusFactory.newInstance(code, msg);
    }

    /**
     * 找Controller中的ValidationStatusCode注解
     * 当前方法->当前Controller类
     *
     * @return
     * @throws Exception
     */
    private ValidationStatusCode findValidationStatusCodeInController() {
        Method method = this.currentControllerMethod();
        //Controller方法上的注解
        ValidationStatusCode validateStatusCode = method.getAnnotation(ValidationStatusCode.class);
        //Controller类上的注解
        if (validateStatusCode == null) {
            validateStatusCode = method.getDeclaringClass().getAnnotation(ValidationStatusCode.class);
        }
        return validateStatusCode;
    }

    @Override
    public ResponseEntity<Response> process(Response response, Throwable throwable) {
        //HTTP ResponseStatus的处理
        ValidationStatusCode validateStatusCode = (ValidationStatusCode) RequestContextHolder.currentRequestAttributes()
                .getAttribute(VALIDATION_STATUS_CODE, RequestAttributes.SCOPE_REQUEST);

        HttpStatusCode httpStatusCode = null;
        if (Objects.nonNull(validateStatusCode)) {
            int httpStatusCodeInt = validateStatusCode.httpStatusCode();
            if (httpStatusCodeInt > 0) {
                httpStatusCode = HttpStatusCode.valueOf(httpStatusCodeInt);
            }
        }

        //找一找有没有ResponseStatus的处理注解，找到的话也设置状态
        org.springframework.web.bind.annotation.ResponseStatus httpResponseStatus = AnnotatedElementUtils.findMergedAnnotation(throwable.getClass(), org.springframework.web.bind.annotation.ResponseStatus.class);

        if (Objects.nonNull(httpResponseStatus)) {
            //取ResponseStatus注解中的状态码
            httpStatusCode = httpResponseStatus.value();
        }

        if (Objects.isNull(httpStatusCode)) {
            //默认的参数校验状态码
            Integer httpStatusCodeInt = properties.getDefaultHttpStatusCodeOnValidationError();
            if (Objects.isNull(httpStatusCodeInt)) {
                //默认的http状态码
                httpStatusCodeInt = properties.getDefaultHttpStatusCodeOnError();
            }
            httpStatusCode = HttpStatusCode.valueOf(httpStatusCodeInt);
        }

        return new ResponseEntity<>(response, httpStatusCode);
    }
}


