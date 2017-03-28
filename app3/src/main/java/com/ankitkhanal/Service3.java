package com.ankitkhanal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * Created by akhanal on 12/21/16.
 */
@RestController
public class Service3 {
    @RequestMapping("/service3")
    public Map<String,String> get3(){
        return Collections.singletonMap("result3","3");
    }
}
