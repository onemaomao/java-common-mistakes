package org.geekbang.time.totry.ratelimter.annotation.controller;

import lombok.extern.slf4j.Slf4j;
import org.geekbang.time.totry.ratelimter.annotation.AccessLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class Controller {


    @GetMapping("test-annotation")
    @AccessLimiter(limit = 1,methodKey = "aaa")
    public String testAnnotation(String hello) {
        return "success";
    }

}
