package com.panda.demo.controller;

import com.panda.demo.endPoint.DemoEndPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class DemoController {

    @Resource
    private DemoEndPoint demoEndPoint;

    @GetMapping("/demo/{userName}")
    public String helloWorld(@PathVariable String userName) {
        demoEndPoint.sendMessageToOne(userName,"hello world");
        return "hello world";
    }
}
