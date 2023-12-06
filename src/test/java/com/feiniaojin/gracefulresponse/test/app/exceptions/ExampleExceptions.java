package com.feiniaojin.gracefulresponse.test.app.exceptions;


import com.feiniaojin.gracefulresponse.api.ExceptionMapper;

/**
 * 测试用例的异常，包括运行时异常和受检异常.
 *
 * @author qinyujie
 * @version 0.1
 */
public class ExampleExceptions {

  @ExceptionMapper(code = "1024", msg = "UnCheckedException")
  public static class UnCheckedException extends RuntimeException {

  }

  @ExceptionMapper(code = "2048", msg = "CheckedException")
  public static class CheckedException extends Exception {

  }

}
