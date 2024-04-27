package com.feiniaojin.gracefulresponse;


import com.feiniaojin.gracefulresponse.advice.*;
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
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

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
    @ConditionalOnMissingBean(value = GrGlobalExceptionAdvice.class)
    public GrGlobalExceptionAdvice grGlobalExceptionAdvice() {
        return new GrGlobalExceptionAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(value = GrValidationExceptionAdvice.class)
    public GrValidationExceptionAdvice grValidationExceptionAdvice() {
        return new GrValidationExceptionAdvice();
    }

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

    @Bean
    @ConditionalOnProperty(prefix = "graceful-response", name = "i18n", havingValue = "true")
    public GrI18nAdvice grI18nAdvice() {
        return new GrI18nAdvice();
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
        messageSource.setBasenames("i18n/graceful-response","i18n/empty-messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.CHINA);
        return messageSource;
    }
}
