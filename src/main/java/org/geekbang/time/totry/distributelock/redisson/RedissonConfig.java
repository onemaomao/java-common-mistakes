package org.geekbang.time.totry.distributelock.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Autowired
    private RedissonProperties redssionProperties;

    //@ConditionalOnProperty(name="redisson.address")
    @Bean
    public RedissonClient redisson(){
        Config config = new Config();
        config.useClusterServers()
                .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                //可以用"rediss://"来启用SSL连接
                .addNodeAddress("redis://192.168.33.43:7001",
                        "redis://192.168.33.43:7002",
                        "redis://192.168.33.43:7003",
                        "redis://192.168.33.43:7004",
                        "redis://192.168.33.43:7005",
                        "redis://192.168.33.43:7006"
                        );
        return Redisson.create(config);


//        SingleServerConfig serverConfig = config.useSingleServer()
//                .setAddress(redssionProperties.getAddress())
//                .setDatabase(redssionProperties.getDatabase());
//        return Redisson.create(config);


    }
}


