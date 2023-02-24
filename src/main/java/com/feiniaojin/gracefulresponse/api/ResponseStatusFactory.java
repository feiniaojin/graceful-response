package com.feiniaojin.gracefulresponse.api;

import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;

import javax.annotation.Resource;

/**
 * @author <a href="mailto:qinyujie@gingo.cn">Yujie</a>
 * @version 0.1
 */
public class ResponseStatusFactory {

    @Resource
    private GracefulResponseProperties properties;

    /**
     * 获得响应成功的ResponseMeta.
     *
     * @return
     */
    public ResponseStatus defaultSuccess() {

        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setCode(properties.getDefaultSuccessCode());
        responseStatus.setMsg(properties.getDefaultSuccessMsg());
        return responseStatus;
    }

    /**
     * 获得失败的ResponseMeta.
     *
     * @return
     */
    public ResponseStatus defaultFail() {
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setCode(properties.getDefaultFailCode());
        responseStatus.setMsg(properties.getDefaultFailMsg());
        return responseStatus;
    }

    /**
     * 从code和msg创建ResponseStatus
     * @param code
     * @param msg
     * @return
     */
    public ResponseStatus newInstance(String code, String msg) {
        return new ResponseStatus(code, msg);
    }

}
