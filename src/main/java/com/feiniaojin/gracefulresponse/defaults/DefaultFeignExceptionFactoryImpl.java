package com.feiniaojin.gracefulresponse.defaults;

import com.feiniaojin.gracefulresponse.api.FeignExceptionFactory;
import com.feiniaojin.gracefulresponse.data.BusinessException;
import com.feiniaojin.gracefulresponse.data.Response;

/**
 * 默认 Feign 响应异常工厂实现
 *
 * @author cp
 * @since 2023-02-23 17:36
 */
public class DefaultFeignExceptionFactoryImpl implements FeignExceptionFactory {

    @Override
    public RuntimeException newInstance(Response response) {
        return new BusinessException(response);
    }

}
