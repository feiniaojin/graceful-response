package com.feiniaojin.gracefulresponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiniaojin.gracefulresponse.api.FeignExceptionFactory;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultFeignExceptionFactoryImpl;
import com.feiniaojin.gracefulresponse.feign.ExceptionDecoder;
import com.feiniaojin.gracefulresponse.feign.ResponseDecoder;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 响应解码器自动注册
 *
 * @author cp
 * @since 2023-02-23 15:17
 */
@Configuration
@ConditionalOnClass(SpringDecoder.class)
public class FeignDecoderAutoConfig {

    @Bean
    @ConditionalOnMissingBean(value = {Decoder.class})
    public Decoder feignDecoder(ObjectProvider<HttpMessageConverters> converters,
                                ObjectProvider<HttpMessageConverterCustomizer> customizer,
                                ResponseFactory responseFactory) {
        return new OptionalDecoder((new ResponseEntityDecoder(
                new ResponseDecoder(new SpringDecoder(converters, customizer), responseFactory))));
    }

    @Bean
    @ConditionalOnMissingBean(value = {FeignExceptionFactory.class})
    public FeignExceptionFactory feignExceptionFactory() {
        return new DefaultFeignExceptionFactoryImpl();
    }

    @Bean
    @ConditionalOnMissingBean(value = {ErrorDecoder.class})
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper,
                                     ResponseFactory responseFactory,
                                     FeignExceptionFactory exceptionFactory) {
        return new ExceptionDecoder(objectMapper, responseFactory, exceptionFactory);
    }

}
