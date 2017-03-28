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
/**
 * Created by akhanal on 12/21/16.
 */
@RestController
public class Service1 {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/service1")
    public Map<String,String> get1() {
        Map<String, String> myResult1 = new LinkedHashMap<>();
        myResult1.put("result1", "1");
        Map<String,String> result = restTemplate.getForObject("http://localhost:9092/service2", Map.class);
        myResult1.putAll(result);
        return myResult1;
    }
}
