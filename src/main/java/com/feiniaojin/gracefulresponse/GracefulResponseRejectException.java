package com.feiniaojin.gracefulresponse;

import com.feiniaojin.gracefulresponse.data.ResponseStatus;

/**
 * 拒绝处理的异常
 *
 * @author qinyujie
 */
public class GracefulResponseRejectException extends GracefulResponseException {

    public GracefulResponseRejectException() {
    }

    public GracefulResponseRejectException(String msg) {
        super(msg);
    }

    public GracefulResponseRejectException(String code, String msg) {
        super(code, msg);
    }

    public GracefulResponseRejectException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public GracefulResponseRejectException(String code, String msg, Throwable cause) {
        super(code, msg, cause);
    }

    public GracefulResponseRejectException(ResponseStatus responseStatus, Throwable cause) {
        super(responseStatus, cause);
    }
}
