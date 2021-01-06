package org.geekbang.time.totry.ratelimter.annotation;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class RateLimiterAnnotationApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RateLimiterAnnotationApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}
