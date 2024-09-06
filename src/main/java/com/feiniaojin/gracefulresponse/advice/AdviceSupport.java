package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ExcludeFromGracefulResponse;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Advice的工具类
 *
 * @author qinyujie
 */
public class AdviceSupport {

    private final Logger logger = LoggerFactory.getLogger(AdviceSupport.class);

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Resource
    private GracefulResponseProperties properties;

    /**
     * 判断是否是JSON消息转换器
     *
     * @param clazz
     * @return
     */
    public boolean isJsonHttpMessageConverter(Class<? extends HttpMessageConverter<?>> clazz) {
        return AbstractJsonHttpMessageConverter.class.isAssignableFrom(clazz)
                || AbstractJackson2HttpMessageConverter.class.isAssignableFrom(clazz)
                || clazz.getName().equals(properties.getJsonHttpMessageConverter());
    }

    /**
     * 判断方法是否命中@ExcludeFromGracefulResponse注解
     *
     * @param method
     * @return
     */
    public boolean matchExcludeFromGracefulResponse(Method method) {

        //有ExcludeFromGracefulResponse注解修饰的，也跳过
        if (method.isAnnotationPresent(ExcludeFromGracefulResponse.class)) {
            logger.debug("Graceful Response:方法被@ExcludeFromGracefulResponse注解修饰:methodName={}", method.getName());
            return true;
        }

        //有ExcludeFromGracefulResponse注解修饰的类，也跳过
        if (method.getDeclaringClass().isAnnotationPresent(ExcludeFromGracefulResponse.class)) {
            logger.debug("Graceful Response:类被@ExcludeFromGracefulResponse注解修饰:methodName={}", method.getName());
            return true;
        }

        //有ExcludeFromGracefulResponse注解修饰的返回类型，也跳过
        if (method.getReturnType().isAnnotationPresent(ExcludeFromGracefulResponse.class)) {
            logger.debug("Graceful Response:返回类型被@ExcludeFromGracefulResponse注解修饰:methodName={}", method.getName());
            return true;
        }
        //未命中，返回false
        return false;
    }

    /**
     * 是否匹配被放行注解
     *
     * @param throwable
     * @return
     */
    public boolean isMatchExcludeException(Throwable throwable) {

        //是否匹配异常类型
        Class<? extends Throwable> aClass = throwable.getClass();
        Set<Class<?>> excludeExceptionTypes = properties.getExcludeExceptionTypes();
        if (!CollectionUtils.isEmpty(excludeExceptionTypes)) {
            //直接包含某个异常，匹配
            if (excludeExceptionTypes.contains(aClass)) {
                return true;
            }
            //当前异常是否是某个配置异常的子类
            Optional<Class<?>> classOptional = excludeExceptionTypes.stream()
                    .filter(eClass -> eClass.isAssignableFrom(aClass)).findFirst();
            if (classOptional.isPresent()) {
                return true;
            }
        }

        //根据异常的包路径放行
        String packageName = aClass.getPackageName();
        List<String> excludeExceptionPackages = properties.getExcludeExceptionPackages();
        if (!CollectionUtils.isEmpty(excludeExceptionPackages)) {
            for (String str : excludeExceptionPackages) {
                if (ANT_PATH_MATCHER.match(str, packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
