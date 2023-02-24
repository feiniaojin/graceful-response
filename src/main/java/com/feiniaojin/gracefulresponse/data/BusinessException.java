package com.feiniaojin.gracefulresponse.data;

/**
 * 业务异常
 *
 * @author cp
 * @since 2023-02-23 17:12
 */
public class BusinessException extends RuntimeException {

    private Response response;

    public BusinessException(Response response) {
        super("服务器业务处理异常", null, false, false);
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}
