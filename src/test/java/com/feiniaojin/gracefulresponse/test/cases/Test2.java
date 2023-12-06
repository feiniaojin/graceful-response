package com.feiniaojin.gracefulresponse.test.cases;

import com.feiniaojin.gracefulresponse.test.app.TestApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

/**
 * origin-exception-using-detail-message=false的测试
 * 测试内容：
 * - 异常信息是否加入到Response的msg字段
 * - 断言包装
 */
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.config.location=classpath:application-test2.yaml"})
public class Test2 {
    @Test
    @DisplayName("Test2:异常信息不填充Response对象msg字段+断言")
    public void testAssert0InTest1(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/example/assert0?id=0");
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("1");
        ResultMatcher payloadMatcher = MockMvcResultMatchers.jsonPath("$.status.msg").value("error!");
        actions.andExpectAll(statusMatcher, codeMatcher, payloadMatcher).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Test2:异常信息不填充Response对象msg字段+函数式包装断言")
    public void testAssert1InTest1(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/example/assert1?id=0");
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("1");
        ResultMatcher payloadMatcher = MockMvcResultMatchers.jsonPath("$.status.msg").value("id不等于1");
        actions.andExpectAll(statusMatcher, codeMatcher, payloadMatcher).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Test2:异常信息不填充Response对象msg字段+函数式包装断言+错误码")
    public void testAssert2InTest1(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/example/assert2?id=0");
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("1001");
        ResultMatcher payloadMatcher = MockMvcResultMatchers.jsonPath("$.status.msg").value("id不等于1");
        actions.andExpectAll(statusMatcher, codeMatcher, payloadMatcher).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Test2:异常信息替换:不自定义异常信息")
    public void testCustomExceptionDetailMessage0(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/example/customExceptionDetailMessage0");
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("1222");
        ResultMatcher payloadMatcher = MockMvcResultMatchers.jsonPath("$.status.msg").value("error");
        actions.andExpectAll(statusMatcher, codeMatcher, payloadMatcher).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Test2:异常信息替换:自定义异常信息")
    public void testCustomExceptionDetailMessage1(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/example/customExceptionDetailMessage1");
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("1222");
        ResultMatcher payloadMatcher = MockMvcResultMatchers.jsonPath("$.status.msg").value("我自己定义了异常信息");
        actions.andExpectAll(statusMatcher, codeMatcher, payloadMatcher).andDo(MockMvcResultHandlers.print());
    }
}
