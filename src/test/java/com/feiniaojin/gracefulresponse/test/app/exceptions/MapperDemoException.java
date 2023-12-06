package com.feiniaojin.gracefulresponse.test.app.exceptions;


import com.feiniaojin.gracefulresponse.api.ExceptionMapper;

@ExceptionMapper(code = "1001",msg = "自定义异常信息")
public class MapperDemoException extends RuntimeException{
}
