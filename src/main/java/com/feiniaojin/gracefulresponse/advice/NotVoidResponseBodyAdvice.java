package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.api.NotUseGracefulResponse;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 非空返回值的处理.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
@ControllerAdvice
@Order(value = 1000)
public class NotVoidResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    
    @Resource
    private ResponseFactory responseFactory;

    /**
     * 只处理不返回void的，并且MappingJackson2HttpMessageConverter支持的类型.
     *
     * @param methodParameter 方法参数
     * @param clazz           处理器
     * @return 是否支持
     */
    @Override
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> clazz) {

        return !Objects.requireNonNull(methodParameter.getMethod()).getReturnType().equals(Void.TYPE)
                && MappingJackson2HttpMessageConverter.class.isAssignableFrom(clazz);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> clazz,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        // 如果添加了不需要包装的注解, 则不判断
        if (Boolean.FALSE.equals(methodParameter.getMethod().isAnnotationPresent(NotUseGracefulResponse.class))) {
            if (body == null) {
                return responseFactory.newSuccessInstance();
            } else if (body instanceof Response) {
                return body;
            } else {
                return responseFactory.newSuccessInstance(body);
            }
        }
        return body;
    }
}
