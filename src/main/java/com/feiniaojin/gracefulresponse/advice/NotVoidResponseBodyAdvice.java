package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ExcludeFromGracefulResponse;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
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
    @Resource
    private GracefulResponseProperties properties;

    /**
     * 路径过滤器
     */
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

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
        Method method = methodParameter.getMethod();
        if (Boolean.TRUE.equals(Objects.nonNull(method))
                // 如果类型不为void
                && Boolean.FALSE.equals(method.getReturnType().equals(Void.TYPE))
                // 如果可以被JSON
                && Boolean.TRUE.equals(MappingJackson2HttpMessageConverter.class.isAssignableFrom(clazz))
                // 如果扫描包不为空
                && Boolean.FALSE.equals(CollectionUtils.isEmpty(properties.getScanPackages()))) {
            List<String> scanPackages = properties.getScanPackages();
            // 获取请求的路径名称
            String packageAndClassName = method.getDeclaringClass().getName();
            // 切割类名
            String packageName = packageAndClassName.substring(0, packageAndClassName.lastIndexOf("."));
            // 如果请求的接口在扫描的包下
            return scanPackages.stream().anyMatch(item -> ANT_PATH_MATCHER.match(item, packageName))
                    && !method.isAnnotationPresent(ExcludeFromGracefulResponse.class);
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> clazz,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        if (body == null) {
            return responseFactory.newSuccessInstance();
        } else if (body instanceof Response) {
            return body;
        } else {
            return responseFactory.newSuccessInstance(body);
        }
    }

}
