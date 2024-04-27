package com.feiniaojin.gracefulresponse.advice;

import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Locale;

/**
 * 国际化处理
 *
 * @author feiniaojin
 */
@ControllerAdvice
@Order(2000)
public class I18nAdvice implements ResponseBodyAdvice<Response> {

    private static final String[] emptyArray = new String[0];

    @Resource
    private GracefulResponseProperties properties;

    @Resource
    private MessageSource messageSource;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Response beforeBodyWrite(Response body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //未开启i18n，直接返回
        if (!properties.getI18n()) {
            return body;
        }
        Locale locale = LocaleContextHolder.getLocale();
        if (locale == null) {
            return body;
        }
        ResponseStatus bodyStatus = body.getStatus();
        String code = bodyStatus.getCode();
        String renderMsg = messageSource.getMessage(code, emptyArray, null, locale);
        //有国际化配置的才会替换，否则使用默认配置的
        if (StringUtils.hasText(renderMsg)) {
            bodyStatus.setMsg(renderMsg);
        }
        return body;
    }
}
