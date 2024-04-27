package com.feiniaojin.gracefulresponse.api;

import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;

/**
 * ResponseBean的工厂类，用于生成ResponseBean.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 */
public interface ResponseFactory {


    /**
     * 创建新的空响应.
     *
     * @return
     */
    Response newEmptyInstance();

    /**
     * 创建新的空响应.
     *
     * @param statusLine 响应行信息.
     * @return
     */
    Response newInstance(ResponseStatus statusLine);

    /**
     * 创建带有数据的响应.
     *
     * @param statusLine 响应行信息
     * @param data 响应数据
     * @return
     */
    Response newInstance(ResponseStatus statusLine,Object data);

    /**
     * 创建新的响应.
     *
     * @return
     */
    Response newSuccessInstance();

    /**
     * 从数据中创建成功响应.
     *
     * @param data 响应数据.
     * @return
     */
    Response newSuccessInstance(Object data);

    /**
     * 创建新的失败响应.
     *
     * @return
     */
    Response newFailInstance();

}
