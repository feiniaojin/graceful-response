package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.advice.lifecycle.response.ResponseBodyAdvicePredicate;
import com.feiniaojin.gracefulresponse.advice.lifecycle.response.ResponseBodyAdviceProcessor;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 非空返回值的处理.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
@ControllerAdvice
@Order(value = 1000)
public class GrNotVoidResponseBodyAdvice extends AbstractResponseBodyAdvice implements ResponseBodyAdvicePredicate,
        ResponseBodyAdviceProcessor {

    private final Logger logger = LoggerFactory.getLogger(GrNotVoidResponseBodyAdvice.class);

    @Resource
    private ResponseFactory responseFactory;

    @Resource
    private GracefulResponseProperties properties;

    @Resource
    private AdviceSupport adviceSupport;

    /**
     * 路径过滤器
     */
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public Object process(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return responseFactory.newSuccessInstance();
        } else if (body instanceof Response) {
            return body;
        } else {
            if (logger.isDebugEnabled()) {
                String path = request.getURI().getPath();
                logger.debug("Graceful Response:非空返回值，执行封装:path={}", path);
            }
            return responseFactory.newSuccessInstance(body);
        }
    }

    @Override
    public boolean shouldApplyTo(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> clazz) {

        Method method = methodParameter.getMethod();

        //method为空、返回值为void、非JSON，直接跳过
        if (Objects.isNull(method)
                || method.getReturnType().equals(Void.TYPE)
                || method.getReturnType().equals(Response.class)
                || !adviceSupport.isJsonHttpMessageConverter(clazz)) {
            logger.debug("Graceful Response:method为空、返回值为void和Response类型、非JSON，跳过");
            return false;
        }

        //命中@ExcludeFromGracefulResponse注解，返回false，不处理
        if (adviceSupport.matchExcludeFromGracefulResponse(method)) {
            return false;
        }

        //配置了例外包路径，则该路径下的controller都不再处理
        List<String> excludePackages = properties.getExcludePackages();
        if (!CollectionUtils.isEmpty(excludePackages)) {
            // 获取请求所在类的的包名
            String packageName = method.getDeclaringClass().getPackage().getName();
            if (excludePackages.stream().anyMatch(item -> ANT_PATH_MATCHER.match(item, packageName))) {
                logger.debug("Graceful Response:匹配到excludePackages例外配置，跳过:packageName={},", packageName);
                return false;
            }
        }

        //配置了例外的返回类型，则不处理
        Set<Class<?>> excludeReturnTypes = properties.getExcludeReturnTypes();
        if (!CollectionUtils.isEmpty(excludeReturnTypes)
                && excludeReturnTypes.contains(method.getReturnType())) {
            logger.debug("Graceful Response:匹配到excludeReturnTypes例外配置，跳过:returnType={},", method.getReturnType());
            return false;
        }

        List<String> excludeUrls = properties.getExcludeUrls();
        if (!CollectionUtils.isEmpty(excludeUrls)) {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String requestUri = request.getRequestURI();
            for (String excludeUrl : excludeUrls) {
                if (ANT_PATH_MATCHER.match(excludeUrl, requestUri)) {
                    logger.debug("Graceful Response:匹配到excludeUrls例外配置，跳过:excludeUrl={},requestURI={}",
                            excludeUrl, requestUri);
                    return false;
                }
            }
        }

        //配置了异常放行，就不会再封装了
        Exception releaseException = (Exception) RequestContextHolder.getRequestAttributes().getAttribute(ReleaseExceptionHandlerExceptionResolver.RELEASE_EXCEPTION_KEY,
                RequestAttributes.SCOPE_REQUEST);
        if (Objects.nonNull(releaseException)
                && adviceSupport.isMatchExcludeException(releaseException)) {
            return false;
        }

        logger.debug("Graceful Response:非空返回值，需要进行封装");
        return true;
    }
}
