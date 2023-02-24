package com.feiniaojin.gracefulresponse.feign;

import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import feign.FeignException;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Feign 正常响应解码器
 *
 * @author cp
 * @since 2023-02-22 16:34
 */
public class ResponseDecoder implements Decoder {

    private final Decoder decoder;

    private final ResponseFactory responseFactory;

    public ResponseDecoder(Decoder decoder, ResponseFactory responseFactory) {
        this.decoder = decoder;
        this.responseFactory = responseFactory;
    }

    @Override
    public Object decode(feign.Response response, Type type) throws IOException, FeignException {
        Method method = response.request().requestTemplate().methodMetadata().method();
        Class<?> responseClass = responseFactory.getResponseClass();
        boolean needUnwrap = method.getReturnType() != responseClass;

        if (needUnwrap) {
            Type wrapType =
                    new ParameterizedType() {
                        @Override
                        public Type[] getActualTypeArguments() {
                            return new Type[]{type};
                        }

                        @Override
                        public Type getRawType() {
                            return responseClass;
                        }

                        @Override
                        public Type getOwnerType() {
                            return null;
                        }
                    };

            Response result = (Response) this.decoder.decode(response, wrapType);
            return result.getPayload();
        }

        return this.decoder.decode(response, type);
    }
}
