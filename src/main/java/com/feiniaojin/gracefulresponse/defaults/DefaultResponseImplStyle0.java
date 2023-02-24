package com.feiniaojin.gracefulresponse.defaults;


import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;

/**
 * 默认的Response实现
 * 包装成统一响应的JavaBean.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
public class DefaultResponseImplStyle0<T> implements Response<T> {

    private ResponseStatus status;
    
    private T payload = null;

    public DefaultResponseImplStyle0() {
    }

    public DefaultResponseImplStyle0(T payload) {
        this.payload = payload;
    }

    @Override
    public void setStatus(ResponseStatus responseStatus) {
        this.status = responseStatus;
    }

    @Override
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public void setPayload(T obj) {
        this.payload = obj;
    }

    @Override
    public T getPayload() {
        return payload;
    }
}
