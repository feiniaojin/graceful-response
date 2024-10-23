package com.feiniaojin.gracefulresponse;

import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.feiniaojin.gracefulresponse.advice.AdviceSupport;
import com.feiniaojin.gracefulresponse.advice.DataExceptionAdvice;
import com.feiniaojin.gracefulresponse.advice.DefaultBeforeControllerAdviceProcessImpl;
import com.feiniaojin.gracefulresponse.advice.DefaultGlobalExceptionAdvice;
import com.feiniaojin.gracefulresponse.advice.DefaultRejectStrategyImpl;
import com.feiniaojin.gracefulresponse.advice.DefaultValidationExceptionAdvice;
import com.feiniaojin.gracefulresponse.advice.FrameworkExceptionAdvice;
import com.feiniaojin.gracefulresponse.advice.GrNotVoidResponseBodyAdvice;
import com.feiniaojin.gracefulresponse.advice.GrVoidResponseBodyAdvice;
import com.feiniaojin.gracefulresponse.advice.ReleaseExceptionHandlerExceptionResolver;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.BeforeControllerAdviceProcess;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdvicePredicate;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.RejectStrategy;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatusFactoryImpl;

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
        return new GrNotVoidResponseBodyAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(GrVoidResponseBodyAdvice.class)
    public GrVoidResponseBodyAdvice grVoidResponseBodyAdvice() {
        return new GrVoidResponseBodyAdvice();
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
