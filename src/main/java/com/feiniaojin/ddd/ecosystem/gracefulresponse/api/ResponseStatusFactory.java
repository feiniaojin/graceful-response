package com.feiniaojin.ddd.ecosystem.gracefulresponse.api;

import com.feiniaojin.ddd.ecosystem.gracefulresponse.data.ResponseStatus;

/**
 * @author <a href="mailto:qinyujie@gingo.cn">Yujie</a>
 * @version 0.1
 */
public interface ResponseStatusFactory {
    /**
     * 获得响应成功的ResponseMeta.
     *
     * @return
     */
    ResponseStatus defaultSuccess();

    /**
     * 获得失败的ResponseMeta.
     *
     * @return
     */
    ResponseStatus defaultFail();


    /**
     * 从code和msg创建ResponseStatus
     * @param code
     * @param msg
     * @return
     */
    ResponseStatus newInstance(String code,String msg);

}
