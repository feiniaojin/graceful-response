package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import jakarta.annotation.Resource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter;

/**
 * Advice的工具类
 * @author qinyujie
 */
public class AdviceSupport {

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

}
