package com.feiniaojin.gracefulresponse.defaults;


import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;

import javax.annotation.Resource;

/**
 * 提供的默认的ResponseMetaFactory实现.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 */
public class DefaultResponseStatusFactoryImpl implements ResponseStatusFactory {

    @Resource
    private GracefulResponseProperties properties;

    @Override
    public ResponseStatus defaultSuccess() {

        DefaultResponseStatus defaultResponseStatus = new DefaultResponseStatus();
        defaultResponseStatus.setCode(properties.getDefaultSuccessCode());
        defaultResponseStatus.setMsg(properties.getDefaultSuccessMsg());
        return defaultResponseStatus;
    }

    @Override
    public ResponseStatus defaultError() {
        DefaultResponseStatus defaultResponseStatus = new DefaultResponseStatus();
        defaultResponseStatus.setCode(properties.getDefaultErrorCode());
        defaultResponseStatus.setMsg(properties.getDefaultErrorMsg());
        return defaultResponseStatus;
    }

    @Override
    public ResponseStatus newInstance(String code, String msg) {
        return new DefaultResponseStatus(code, msg);
    }
}
