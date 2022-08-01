package com.feiniaojin.ddd.ecosystem.gracefulresponse.defaults;


import com.feiniaojin.ddd.ecosystem.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.ddd.ecosystem.gracefulresponse.data.ResponseStatus;

/**
 * 提供的默认的ResponseMetaFactory实现.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 */
public class DefaultResponseStatusFactoryImpl implements ResponseStatusFactory {

    @Override
    public ResponseStatus defaultSuccess() {
        return DefaultResponseStatus.DEFAULT_SUCCESS;
    }

    @Override
    public ResponseStatus defaultFail() {
        return DefaultResponseStatus.DEFAULT_FAIL;
    }

    @Override
    public ResponseStatus newInstance(String code, String msg) {
        return new DefaultResponseStatus(code, msg);
    }
}
