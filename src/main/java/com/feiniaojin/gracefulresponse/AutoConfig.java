package com.feiniaojin.gracefulresponse;


import com.feiniaojin.gracefulresponse.advice.GlobalExceptionAdvice;
import com.feiniaojin.gracefulresponse.advice.NotVoidResponseBodyAdvice;
import com.feiniaojin.gracefulresponse.advice.VoidResponseBodyAdvice;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatusFactoryImpl;
import com.feiniaojin.gracefulresponse.feign.ResponseDecoder;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @ConditionalOnMissingBean(value = GlobalExceptionAdvice.class)
    public GlobalExceptionAdvice globalExceptionAdvice() {
        return new GlobalExceptionAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(NotVoidResponseBodyAdvice.class)
    public NotVoidResponseBodyAdvice notVoidResponseBodyAdvice() {
        return new NotVoidResponseBodyAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(VoidResponseBodyAdvice.class)
    public VoidResponseBodyAdvice voidResponseBodyAdvice() {
        return new VoidResponseBodyAdvice();
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
    @ConditionalOnMissingBean(value = {SpringDecoder.class})
    public Decoder feignDecoder(ObjectProvider<HttpMessageConverters> messageConverters, ResponseFactory responseFactory) {
        return new OptionalDecoder((new ResponseEntityDecoder(new ResponseDecoder(new SpringDecoder(messageConverters), responseFactory))));
    }
}
