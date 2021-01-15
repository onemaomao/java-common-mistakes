package org.geekbang.time.totry.distributelock;

import org.geekbang.time.commonmistakes.common.Utils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.geekbang.time.totry.distributelock")
public class DistributeLockApplication {

    public static void main(String[] args) {
        Utils.loadPropertySource(DistributeLockApplication.class, "distributelock.properties");
        SpringApplication.run(DistributeLockApplication.class, args);
    }

}
