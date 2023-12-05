package com.feiniaojin;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 模拟端口，随机的
@AutoConfigureMockMvc // 开启虚拟 MVC 调用
public class ApplicationTest {

    @Test
    public void testBizNormal(/* 注入虚拟 MVC 调用对象 */@Autowired MockMvc mockMvc) throws Exception {
        // 创建虚拟请求，访问接口
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/biz/2");
        // 执行请求
        ResultActions actions = mockMvc.perform(requestBuilder);
        // 定义执行结果匹配器
        ContentResultMatchers content = MockMvcResultMatchers.content();
        // 定义预期执行结果
        ResultMatcher result = content.json(""" 
                  {
                 	"code": "0",
                 	"msg": "ok",
                 	"data": {
                 		"hello": "world"
                 	}
                 }""");
        // 进行匹配
        actions.andExpect(result);
    }

    @Test
    public void testBizException(/* 注入虚拟 MVC 调用对象 */@Autowired MockMvc mockMvc) throws Exception{
        // 创建虚拟请求，访问接口
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/biz/1");
        // 执行请求
        ResultActions actions = mockMvc.perform(requestBuilder);
        // 定义执行结果匹配器
        ContentResultMatchers content = MockMvcResultMatchers.content();
        // 定义预期执行结果
        ResultMatcher result = content.json(""" 
                  
                {
                  	"code": "500",
                  	"msg": "业务异常",
                  	"data": {}
                  }""");
        // 进行匹配
        actions.andExpect(result);
    }

}
