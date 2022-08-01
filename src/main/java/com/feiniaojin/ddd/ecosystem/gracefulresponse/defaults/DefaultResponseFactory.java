package com.feiniaojin.ddd.ecosystem.gracefulresponse.defaults;


import com.feiniaojin.ddd.ecosystem.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.ddd.ecosystem.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.ddd.ecosystem.gracefulresponse.data.Response;
import com.feiniaojin.ddd.ecosystem.gracefulresponse.data.ResponseStatus;

import javax.annotation.Resource;

/**
 * 提供的默认的ResponseBeanFactory实现.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 */
public class DefaultResponseFactory implements ResponseFactory {

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    @Override
    public Response newEmptyInstance() {
        return new DefaultResponse();
    }

    @Override
    public Response newInstance(ResponseStatus responseStatus) {
        Response bean = this.newEmptyInstance();
        bean.setStatus(responseStatus);
        return bean;
    }

    @Override
    public Response newSuccessInstance() {
        DefaultResponse responseBean = new DefaultResponse();
        responseBean.setStatus(responseStatusFactory.defaultSuccess());
        return responseBean;
    }

    @Override
    public Response newSuccessInstance(Object payload) {
        Response bean = this.newSuccessInstance();
        bean.setPayload(payload);
        return bean;
    }

    @Override
    public Response newFailInstance() {
        Response bean = this.newEmptyInstance();
        bean.setStatus(responseStatusFactory.defaultFail());
        return bean;
    }

}
