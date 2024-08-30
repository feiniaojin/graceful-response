package com.feiniaojin.gracefulresponse.test.app.controller;

import com.feiniaojin.gracefulresponse.GracefulResponse;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ValidationStatusCode;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.test.app.dto.UserInfoCommand;
import com.feiniaojin.gracefulresponse.test.app.dto.UserInfoQuery;
import com.feiniaojin.gracefulresponse.test.app.dto.UserInfoView;
import com.feiniaojin.gracefulresponse.test.app.exceptions.ReplaceMsgException;
import com.feiniaojin.gracefulresponse.test.app.service.ExampleService;
import com.feiniaojin.gracefulresponse.test.app.vo.Result;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;


/**
 * class {@code ExampleController} 使用案例的Controller.
 *
 * @author qinyujie
 */
@Controller
@RequestMapping("/example")
@Validated
public class ExampleController {
    private Logger logger = LoggerFactory.getLogger(ExampleController.class);

    @Resource
    private ResponseFactory responseFactory;

    @Resource
    private ExampleService exampleService;

    /**
     * 测试空返回值.
     * http://localhost:9090/example/void
     */
    @RequestMapping("/voidResponse")
    @ResponseBody
    public void voidResponse() {
        logger.info("testVoidResponse");
    }


    /**
     * 测试成功返回
     * http://localhost:9090/example/success?userId=1&userName=userName&age=20
     *
     * @param dto
     * @return
     */
    @RequestMapping("/success")
    @ResponseBody
    public UserInfoView testSuccess(UserInfoQuery dto) {
        logger.info(dto.toString());
        UserInfoView view = new UserInfoView();
        view.setId(1L);
        view.setName("name");
        return view;
    }

    /**
     * 测试成功返回
     * http://localhost:9090/example/get?id=1
     *
     * @param id
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public UserInfoView get(Long id) {
        logger.info("id={}", id);
        UserInfoView view = new UserInfoView();
        view.setId(1L);
        view.setName("name" + id);
        return view;
    }

    /**
     * 测试抛出运行时异常的处理.
     * http://localhost:9090/example/runtime
     *
     * @return 直接返回，未处理
     */
    @RequestMapping("/runtime")
    @ResponseBody
    public UserInfoView testRuntimeException() {
        exampleService.testUnCheckedException();
        UserInfoView view = new UserInfoView();
        view.setId(1L);
        view.setName("0000");
        return view;
    }

    /**
     * 测试受检异常的情形.
     * http://localhost:9090/example/checked
     *
     * @param dto 入参
     * @return 未处理，直接将入参返回
     * @throws Exception 受检异常
     */
    @RequestMapping("/checked")
    @ResponseBody
    public UserInfoView testCheckedException(UserInfoQuery dto) throws Exception {
        logger.info(dto.toString());
        exampleService.testCheckedException();
        UserInfoView view = new UserInfoView();
        view.setId(1L);
        view.setName("0000");
        return view;
    }

    /**
     * 测试抛出{@code Throwable} 的情形.
     * http://localhost:9090/example/throwable
     *
     * @param dto 入参
     * @return 未处理，直接返回
     * @throws Throwable 抛出Throwable异常
     */
    @RequestMapping("/throwable")
    @ResponseBody
    public UserInfoView testThrowable(UserInfoQuery dto) throws Throwable {
        logger.info(dto.toString());
        throw new Throwable();
    }

    /**
     * http://localhost:9090/example/validateDto
     *
     * @param dto
     */
    @RequestMapping("/validateDto")
    @ResponseBody
    public void validateDto(@Validated UserInfoQuery dto) {
        logger.info(dto.toString());
    }

    /**
     * 测试Controller中方法对参数进行校验的情形.
     * http://localhost:9090/example/validateMethodParam
     *
     * @param userId 非空
     */
    @RequestMapping("/validateMethodParam")
    @ResponseBody
    @ValidationStatusCode(code = "1314")
    public void validateMethodParam(@RequestParam(name = "userId", required = false) @NotNull(message = "userId不能为空") Long userId,
                                    @RequestParam(name = "userName", required = false) @NotNull(message = "userName不能为空") Long userName) {
        logger.info("userId={}", userId);
    }

    /**
     * 不支持的http方法调用.
     * POST接口，使用GET进行请求
     * http://localhost:9090/example/methodPost
     *
     * @param userId 非空
     */
    @RequestMapping(value = "/methodPost", method = RequestMethod.POST)
    @ResponseBody
    public void testMethodNotSupport(Long userId) {
        logger.info("userId={}", userId);

    }

    /**
     * http://localhost:9090/example/jsonStr
     * <p>
     * {"key":"value"}
     * 测试Controller中方法对参数进行校验的情形.
     */
    @RequestMapping("/jsonStr")
    @ResponseBody
    public Response jsonStr() {
        logger.info("");
        return responseFactory.newSuccessInstance("abc");
    }

    /**
     * http://localhost:9090/example/str
     * 直接返回String，将会匹配模版页面.
     */
    @RequestMapping("/str")
    public String str() {
        logger.info("");
        return "view";
    }


    /**
     * 测试Controller中方法对参数进行校验的情形.
     * http://localhost:9090/example/raiseException0
     */
    @RequestMapping("/raiseException0")
    public void raiseException0() {
        GracefulResponse.raiseException("520", "测试手工异常0");
    }

    /**
     * 测试Controller中方法对参数进行校验的情形.
     * http://localhost:9090/example/raiseException0
     */
    @RequestMapping("/raiseException1")
    public void raiseException1() {
        try {
            throw new Exception("发生异常啦");
        } catch (Exception e) {
            GracefulResponse.raiseException("1314", "测试手工异常1", e);
        }
    }

    /**
     * http://localhost:9090/example/return/str
     * 直接返回String
     */
    @RequestMapping("/return/str")
    @ResponseBody
    public String returnStr() {
        return "测试成功";
    }

    /**
     * http://localhost:9090/example/validate/propertyType
     *
     * @param command
     */
    @RequestMapping("/validate/propertyType")
    @ResponseBody
    public void validatePropertyType(@RequestBody @Validated UserInfoCommand command) {
        logger.info("");
    }

    @ResponseBody
    @RequestMapping("/kv")
    public Map<String, Object> single() {
        return Collections.singletonMap("key", "token");
    }

    @RequestMapping("/assert0")
    @ResponseBody
    public void assert0(Integer id) {
        Assert.isTrue(id == 1, "id不等于1");
    }

    @RequestMapping("/assert1")
    @ResponseBody
    public void assert1(Integer id) {
        GracefulResponse.wrapAssert(() -> Assert.isTrue(id == 1, "id不等于1"));
    }

    @RequestMapping("/assert2")
    @ResponseBody
    public void assert2(Integer id) {
        GracefulResponse.wrapAssert("1001", () -> Assert.isTrue(id == 1, "id不等于1"));
    }

    @RequestMapping("/assert3")
    @ResponseBody
    public void assert3(Integer id) {
        logger.info("assert3");
        GracefulResponse.wrapAssert("1001", "data", () -> Assert.isTrue(id == 1, "id不等于1"));
    }

    @RequestMapping("/customExceptionDetailMessage0")
    public void customExceptionDetailMessage0() {
        throw new ReplaceMsgException();
    }

    @RequestMapping("/customExceptionDetailMessage1")
    public void customExceptionDetailMessage1() {
        throw new ReplaceMsgException("我自己定义了异常信息");
    }

    /**
     * 测试直接返回自定义对象的情况
     * http://localhost:9090/example/result
     */
    @RequestMapping("/result")
    @ResponseBody
    public Result result() {
        Result result = new Result();
        result.setCode(0);
        result.setMsg("Ok");
        return result;
    }
}
