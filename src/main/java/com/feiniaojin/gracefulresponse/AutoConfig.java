package com.feiniaojin.gracefulresponse;


import com.feiniaojin.gracefulresponse.advice.*;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.BeforeControllerAdviceProcess;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdvicePredicate;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.RejectStrategy;
import com.feiniaojin.gracefulresponse.advice.lifecycle.response.ResponseBodyAdvicePredicate;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatusFactoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 全局返回值处理的自动配置.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
@Configuration
@EnableConfigurationProperties(GracefulResponseProperties.class)
public class AutoConfig {

    @Bean
    @ConditionalOnMissingBean(GrNotVoidResponseBodyAdvice.class)
    public GrNotVoidResponseBodyAdvice grNotVoidResponseBodyAdvice() {
        GrNotVoidResponseBodyAdvice notVoidResponseBodyAdvice = new GrNotVoidResponseBodyAdvice();
        CopyOnWriteArrayList<ResponseBodyAdvicePredicate> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add(notVoidResponseBodyAdvice);
        notVoidResponseBodyAdvice.setPredicates(copyOnWriteArrayList);
        notVoidResponseBodyAdvice.setResponseBodyAdviceProcessor(notVoidResponseBodyAdvice);
        return notVoidResponseBodyAdvice;
    }

    @Bean
    @ConditionalOnMissingBean(GrVoidResponseBodyAdvice.class)
    public GrVoidResponseBodyAdvice grVoidResponseBodyAdvice() {
        GrVoidResponseBodyAdvice voidResponseBodyAdvice = new GrVoidResponseBodyAdvice();
        CopyOnWriteArrayList<ResponseBodyAdvicePredicate> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add(voidResponseBodyAdvice);
        voidResponseBodyAdvice.setPredicates(copyOnWriteArrayList);
        voidResponseBodyAdvice.setResponseBodyAdviceProcessor(voidResponseBodyAdvice);
        return voidResponseBodyAdvice;
    }

    @Bean
    @ConditionalOnMissingBean(value = {ResponseFactory.class})
    public ResponseFactory responseBeanFactory() {
        return new DefaultResponseFactory();
    }

    @Bean
    @ConditionalOnMissingBean(value = {ResponseStatusFactory.class})
    public ResponseStatusFactory responseStatusFactory() {
        return new DefaultResponseStatusFactoryImpl();
    }

    @Bean
    public ExceptionAliasRegister exceptionAliasRegister() {
        return new ExceptionAliasRegister();
    }

    @Bean
    public AdviceSupport adviceSupport() {
        return new AdviceSupport();
    }

    @Bean
    public Init init() {
        return new Init();
    }

    @Bean
    @ConditionalOnProperty(prefix = "graceful-response", name = "i18n", havingValue = "true")
    public GrI18nResponseBodyAdvice grI18nAdvice() {
        GrI18nResponseBodyAdvice i18nResponseBodyAdvice = new GrI18nResponseBodyAdvice();
        CopyOnWriteArrayList<ResponseBodyAdvicePredicate> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add(i18nResponseBodyAdvice);
        i18nResponseBodyAdvice.setPredicates(copyOnWriteArrayList);
        i18nResponseBodyAdvice.setResponseBodyAdviceProcessor(i18nResponseBodyAdvice);
        return i18nResponseBodyAdvice;
    }

    /**
     * 国际化配置
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "graceful-response", name = "i18n", havingValue = "true")
    public MessageSource grMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/graceful-response", "i18n/empty-messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.CHINA);
        return messageSource;
    }

    @Bean
    @ConditionalOnMissingBean(value = BeforeControllerAdviceProcess.class)
    public BeforeControllerAdviceProcess beforeAdviceProcess() {
        return new DefaultBeforeControllerAdviceProcessImpl();
    }

    @Bean
    public FrameworkExceptionAdvice frameworkExceptionAdvice(BeforeControllerAdviceProcess beforeControllerAdviceProcess,
                                                             @Lazy RejectStrategy rejectStrategy) {
        FrameworkExceptionAdvice frameworkExceptionAdvice = new FrameworkExceptionAdvice();
        frameworkExceptionAdvice.setRejectStrategy(rejectStrategy);
        frameworkExceptionAdvice.setControllerAdviceProcessor(frameworkExceptionAdvice);
        frameworkExceptionAdvice.setBeforeControllerAdviceProcess(beforeControllerAdviceProcess);
        frameworkExceptionAdvice.setControllerAdviceHttpProcessor(frameworkExceptionAdvice);
        return frameworkExceptionAdvice;
    }

    @Bean
    public DataExceptionAdvice dataExceptionAdvice(BeforeControllerAdviceProcess beforeControllerAdviceProcess,
                                                   @Lazy RejectStrategy rejectStrategy) {
        DataExceptionAdvice dataExceptionAdvice = new DataExceptionAdvice();
        dataExceptionAdvice.setRejectStrategy(rejectStrategy);
        dataExceptionAdvice.setControllerAdviceProcessor(dataExceptionAdvice);
        dataExceptionAdvice.setBeforeControllerAdviceProcess(beforeControllerAdviceProcess);
        dataExceptionAdvice.setControllerAdviceHttpProcessor(dataExceptionAdvice);
        return dataExceptionAdvice;
    }

    @Bean
    public DefaultGlobalExceptionAdvice defaultGlobalExceptionAdvice(BeforeControllerAdviceProcess beforeControllerAdviceProcess,
                                                                     @Lazy RejectStrategy rejectStrategy) {
        DefaultGlobalExceptionAdvice advice = new DefaultGlobalExceptionAdvice();
        advice.setRejectStrategy(rejectStrategy);
        CopyOnWriteArrayList<ControllerAdvicePredicate> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add(advice);
        advice.setPredicates(copyOnWriteArrayList);
        advice.setControllerAdviceProcessor(advice);
        advice.setBeforeControllerAdviceProcess(beforeControllerAdviceProcess);
        advice.setControllerAdviceHttpProcessor(advice);
        return advice;
    }

    @Bean
    public DefaultValidationExceptionAdvice defaultValidationExceptionAdvice(BeforeControllerAdviceProcess beforeControllerAdviceProcess,
                                                                             @Lazy RejectStrategy rejectStrategy) {
        DefaultValidationExceptionAdvice advice = new DefaultValidationExceptionAdvice();
        advice.setRejectStrategy(rejectStrategy);
        advice.setControllerAdviceProcessor(advice);
        advice.setBeforeControllerAdviceProcess(beforeControllerAdviceProcess);
        // 设置默认参数校验异常http处理器
        advice.setControllerAdviceHttpProcessor(advice);
        return advice;
    }

    @Bean
    public RejectStrategy rejectStrategy() {
        return new DefaultRejectStrategyImpl();
    }

    @Bean
    public ExceptionHandlerExceptionResolver releaseExceptionHandlerExceptionResolver() {
        return new ReleaseExceptionHandlerExceptionResolver();
    }
}
