package com.ankitkhanal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by akhanal on 12/21/16.
 */
@RestController
public class Service2 {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/service2")
    public Map<String,String> get2() {
        Map<String, String> myResult2 = new LinkedHashMap<>();
        myResult2.put("result2", "2");
        Map<String,String> result = restTemplate.getForObject("http://localhost:9093/service3", Map.class);
        myResult2.putAll(result);
        return myResult2;
    }
}
