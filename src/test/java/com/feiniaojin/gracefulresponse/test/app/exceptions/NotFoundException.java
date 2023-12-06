package com.feiniaojin.gracefulresponse.test.app.exceptions;

import com.feiniaojin.gracefulresponse.api.ExceptionAliasFor;
import org.springframework.web.servlet.NoHandlerFoundException;

@ExceptionAliasFor(code = "1404", msg = "not found", aliasFor = NoHandlerFoundException.class)
public class NotFoundException extends RuntimeException {
}
