package com.feiniaojin.gracefulresponse.test.app.exceptions;

import com.feiniaojin.gracefulresponse.api.ExceptionMapper;

@ExceptionMapper(code = "1222", msg = "error", msgReplaceable = true)
public class ReplaceMsgException extends RuntimeException {
    public ReplaceMsgException() {
    }

    public ReplaceMsgException(String message) {
        super(message);
    }

    public ReplaceMsgException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReplaceMsgException(Throwable cause) {
        super(cause);
    }

    public ReplaceMsgException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
