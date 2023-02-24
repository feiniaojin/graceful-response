package com.feiniaojin.gracefulresponse.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiniaojin.gracefulresponse.api.FeignExceptionFactory;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.nio.charset.StandardCharsets;

/**
 * Feign 异常响应解码器
 *
 * @author cp
 * @since 2023-02-23 22:06
 */
public class ExceptionDecoder implements ErrorDecoder {

    private ObjectMapper objectMapper;

    private ResponseFactory responseFactory;

    private FeignExceptionFactory exceptionFactory;

    public ExceptionDecoder(ObjectMapper objectMapper,
                            ResponseFactory responseFactory,
                            FeignExceptionFactory exceptionFactory) {
        this.objectMapper = objectMapper;
        this.responseFactory = responseFactory;
        this.exceptionFactory = exceptionFactory;
    }

    @Override
    public Exception decode(String methodKey, feign.Response response) {
        if (response.status() == 500) {
            try {
                String message = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                Response result = (Response) objectMapper.readValue(message, responseFactory.getResponseClass());
                return exceptionFactory.newInstance(result);
            } catch (Exception ignored) {

            }
        }

        return new ErrorDecoder.Default().decode(methodKey, response);
    }

}
