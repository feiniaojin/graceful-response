package com.feiniaojin.gracefulresponse.test.cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiniaojin.gracefulresponse.test.app.TestApplication;
import com.feiniaojin.gracefulresponse.test.app.dto.ExtendProperties;
import com.feiniaojin.gracefulresponse.test.app.dto.UserInfoCommand;
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

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.config.location=classpath:application-test0.yaml"})
public class Test0 {
    @Test
    @DisplayName("测试返回空")
    public void testVoidResponse(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/example/voidResponse");
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("0");
        actions.andExpectAll(statusMatcher, codeMatcher).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试执行成功统一封装")
    public void testSuccess(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/example/success");
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("0");
        ResultMatcher payloadMatcher = MockMvcResultMatchers.jsonPath("$.payload").isNotEmpty();
        actions.andExpectAll(statusMatcher, codeMatcher, payloadMatcher).andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("自定义异常+异常码+运行时异常")
    public void testRuntime(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/example/runtime");
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("1024");
        ResultMatcher payloadMatcher = MockMvcResultMatchers.jsonPath("$.payload").isEmpty();
        actions.andExpectAll(statusMatcher, codeMatcher, payloadMatcher).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("DTO内参数校验")
    public void testValidateDto(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/example/validateDto");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userName","userName");
        ObjectMapper objectMapper = new ObjectMapper();
        String reqBody = objectMapper.writeValueAsString(paramMap);
        requestBuilder.content(reqBody);
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("1");
        ResultMatcher payloadMatcher = MockMvcResultMatchers.jsonPath("$.payload").isEmpty();
        actions.andExpectAll(statusMatcher, codeMatcher, payloadMatcher).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Controller方法入参校验")
    public void testValidateMethodParam(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/example/validateMethodParam");
        Map<String, Object> paramMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String reqBody = objectMapper.writeValueAsString(paramMap);
        requestBuilder.content(reqBody);
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("1314");
        ResultMatcher payloadMatcher = MockMvcResultMatchers.jsonPath("$.payload").isEmpty();
        actions.andExpectAll(statusMatcher, codeMatcher, payloadMatcher).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("级联校验")
    public void testValidatePropertyType(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/example/validate/propertyType");
        UserInfoCommand command=new UserInfoCommand();
        command.setUserId(1L);
        command.setUserName("userName");
        ExtendProperties properties = new ExtendProperties();
        command.setExtendProperties(properties);
        ObjectMapper objectMapper = new ObjectMapper();
        String reqBody = objectMapper.writeValueAsString(command);
        requestBuilder.content(reqBody);
        requestBuilder.contentType("application/json");
        ResultActions actions = mockMvc.perform(requestBuilder);
        StatusResultMatchers status = MockMvcResultMatchers.status();
        ResultMatcher statusMatcher = status.is(200);

        ResultMatcher codeMatcher = MockMvcResultMatchers.jsonPath("$.status.code").value("1");
        ResultMatcher payloadMatcher = MockMvcResultMatchers.jsonPath("$.payload").isEmpty();
        actions.andExpectAll(statusMatcher, codeMatcher, payloadMatcher).andDo(MockMvcResultHandlers.print());
    }
}
