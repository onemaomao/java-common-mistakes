package org.geekbang.time.totry.bloomfilter;

import org.geekbang.time.commonmistakes.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.geekbang.time.totry.bloomfilter")
public class BloomFilterApplication {

    public static void main(String[] args) {
        Utils.loadPropertySource(BloomFilterApplication.class, "bloomfilter.properties");
        SpringApplication.run(BloomFilterApplication.class, args);
    }

}
