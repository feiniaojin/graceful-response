package com.feiniaojin.gracefulresponse.test.app.exceptions;

import com.feiniaojin.gracefulresponse.api.ExceptionAliasFor;
import org.springframework.web.client.HttpClientErrorException;

@ExceptionAliasFor(code = "1404",msg = "自定义异常信息",
        aliasFor = HttpClientErrorException.NotFound.class)
public class AliasDemoException extends RuntimeException{
}
