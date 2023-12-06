package com.feiniaojin.gracefulresponse.test.app.exclude;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/exclude")
public class ExcludeController {

    @RequestMapping("/test")
    @ResponseBody
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("key", "value");
        return result;
    }
}
