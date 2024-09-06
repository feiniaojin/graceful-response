package com.feiniaojin.gracefulresponse.advice;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.support.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.DisconnectedClientHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 释放异常的ExceptionHandlerExceptionResolver
 * @author qinyujie
 */
public class ReleaseExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver
        implements ApplicationContextAware, InitializingBean {

    public static final String RELEASE_EXCEPTION_KEY = "RELEASE_EXCEPTION_KEY";

    private final List<Object> responseBodyAdvice = new ArrayList<>();

    private final ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();

    private final Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> exceptionHandlerAdviceCache =
            new LinkedHashMap<>();

    private static final String DISCONNECTED_CLIENT_LOG_CATEGORY =
            "org.springframework.web.servlet.mvc.method.annotation.DisconnectedClient";

    private static final DisconnectedClientHelper DISCONNECTED_CLIENT_HELPER =
            new DisconnectedClientHelper(DISCONNECTED_CLIENT_LOG_CATEGORY);

    @Resource
    private ApplicationContext applicationContext;

    @Nullable
    private HandlerMethodArgumentResolverComposite argumentResolvers;

    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    private final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

    private void initExceptionHandlerAdviceCache() {
        if (getApplicationContext() == null) {
            return;
        }

        List<ControllerAdviceBean> adviceBeans = ControllerAdviceBean.findAnnotatedBeans(getApplicationContext());
        for (ControllerAdviceBean adviceBean : adviceBeans) {
            Class<?> beanType = adviceBean.getBeanType();
            if (beanType == null) {
                throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
            }
            ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(beanType);
            if (resolver.hasExceptionMappings()) {
                this.exceptionHandlerAdviceCache.put(adviceBean, resolver);
            }
            if (ResponseBodyAdvice.class.isAssignableFrom(beanType)) {
                this.responseBodyAdvice.add(adviceBean);
            }
        }

        if (logger.isDebugEnabled()) {
            int handlerSize = this.exceptionHandlerAdviceCache.size();
            int adviceSize = this.responseBodyAdvice.size();
            if (handlerSize == 0 && adviceSize == 0) {
                logger.debug("ControllerAdvice beans: none");
            } else {
                logger.debug("ControllerAdvice beans: " +
                        handlerSize + " @ExceptionHandler, " + adviceSize + " ResponseBodyAdvice");
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        // Do this first, it may add ResponseBodyAdvice beans
        initExceptionHandlerAdviceCache();
        initMessageConverters();

        if (this.argumentResolvers == null) {
            List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.returnValueHandlers == null) {
            List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
        returnValueHandlers.addHandler(new RequestResponseBodyMethodProcessor(this.messageConverters,
                getContentNegotiationManager(),
                this.responseBodyAdvice));
    }

    private void initMessageConverters() {
        if (!this.messageConverters.isEmpty()) {
            return;
        }
        this.messageConverters.add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    protected List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();

        // Single-purpose return value types
        handlers.add(new ModelAndViewMethodReturnValueHandler());
        handlers.add(new ModelMethodProcessor());
        handlers.add(new ViewMethodReturnValueHandler());
        handlers.add(new HttpEntityMethodProcessor(
                getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));

        // Annotation-based return value types
        handlers.add(new ServletModelAttributeMethodProcessor(false));
        handlers.add(new RequestResponseBodyMethodProcessor(
                getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));

        // Multi-purpose return value types
        handlers.add(new ViewNameMethodReturnValueHandler());
        handlers.add(new MapMethodProcessor());

        // Custom return value types
        if (getCustomReturnValueHandlers() != null) {
            handlers.addAll(getCustomReturnValueHandlers());
        }

        handlers.add(new ReleaseMessageConverterMethodProcessor(
                getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));

        // Catch-all
        handlers.add(new ServletModelAttributeMethodProcessor(true));

        return handlers;
    }

    @Override
    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    @Override
    @Nullable
    public ModelAndView doResolveHandlerMethodException(HttpServletRequest request,
                                                        HttpServletResponse response, @Nullable HandlerMethod handlerMethod, Exception exception) {

        ServletInvocableHandlerMethod exceptionHandlerMethod = getExceptionHandlerMethod(handlerMethod, exception);
        if (exceptionHandlerMethod == null) {
            return null;
        }

        if (this.argumentResolvers != null) {
            exceptionHandlerMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }
        if (this.returnValueHandlers != null) {
            exceptionHandlerMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
        }

        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();

        ArrayList<Throwable> exceptions = new ArrayList<>();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Using @ExceptionHandler " + exceptionHandlerMethod);
            }
            // Expose causes as provided arguments as well
            Throwable exToExpose = exception;
            while (exToExpose != null) {
                exceptions.add(exToExpose);
                Throwable cause = exToExpose.getCause();
                exToExpose = (cause != exToExpose ? cause : null);
            }
            Object[] arguments = new Object[exceptions.size() + 1];
            // efficient arraycopy call in ArrayList
            exceptions.toArray(arguments);
            arguments[arguments.length - 1] = handlerMethod;
            exceptionHandlerMethod.invokeAndHandle(webRequest, mavContainer, arguments);
        } catch (Exception invocationEx) {
            if (DISCONNECTED_CLIENT_HELPER.checkAndLogClientDisconnectedException(invocationEx)) {
                return new ModelAndView();
            }
            // Any other than the original exception (or a cause) is unintended here,
            // probably an accident (e.g. failed assertion or the like).
            if (!exceptions.contains(invocationEx) && logger.isWarnEnabled()) {
                logger.warn("Failure in @ExceptionHandler " + exceptionHandlerMethod, invocationEx);
            }
            // Continue with default processing of the original exception...
            return null;
        }

        if (mavContainer.isRequestHandled()) {
            return new ModelAndView();
        } else {
            ModelMap model = mavContainer.getModel();
            HttpStatusCode status = mavContainer.getStatus();
            ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model, status);
            mav.setViewName(mavContainer.getViewName());
            if (!mavContainer.isViewReference()) {
                mav.setView((View) mavContainer.getView());
            }
            if (model instanceof RedirectAttributes redirectAttributes) {
                Map<String, ?> flashAttributes = redirectAttributes.getFlashAttributes();
                RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
            }
            return mav;
        }
    }

    @Override
    protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
        return new ServletInvocableHandlerMethod(handlerMethod.getBean(), handlerMethod.getMethod(), this.applicationContext);
    }
}
