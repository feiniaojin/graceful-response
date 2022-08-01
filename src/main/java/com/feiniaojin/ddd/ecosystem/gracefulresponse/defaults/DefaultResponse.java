package com.feiniaojin.ddd.ecosystem.gracefulresponse.defaults;


import com.feiniaojin.ddd.ecosystem.gracefulresponse.data.Response;
import com.feiniaojin.ddd.ecosystem.gracefulresponse.data.ResponseStatus;

import java.util.Collections;
import java.util.Map;

/**
 * 提供默认实现的
 * 包装成统一响应的JavaBean.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
public class DefaultResponse implements Response {

    private static final Map<Object, Object> DEFAULT_NULL = Collections.emptyMap();

    private ResponseStatus status = DefaultResponseStatus.DEFAULT_SUCCESS;
    private Object payload = DEFAULT_NULL;

    public DefaultResponse() {
    }

    public DefaultResponse(Object payload) {
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
    public void setPayload(Object obj) {
        this.payload = obj;
    }

    @Override
    public Object getPayload() {
        return payload;
    }
}
