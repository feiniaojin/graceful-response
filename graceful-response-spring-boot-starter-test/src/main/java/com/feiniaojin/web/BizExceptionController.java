package com.feiniaojin.web;

import com.feiniaojin.ex.BizException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BizExceptionController {

    @GetMapping(value = "/biz/{id}")
    public Map<String,Object> biz(@PathVariable("id") Long id){
        if(id == 1L){
            throw new BizException("业务异常");
        }

        return Map.of("hello","world");
    }

}
