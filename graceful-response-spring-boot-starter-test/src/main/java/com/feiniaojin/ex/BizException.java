package com.feiniaojin.ex;

import com.feiniaojin.gracefulresponse.api.ExceptionMapper;
import lombok.experimental.StandardException;
@ExceptionMapper(code = "500", msg = "有内鬼，终止交易")
@StandardException
public class BizException extends RuntimeException {

}
