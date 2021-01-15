package org.geekbang.time.totry.bloomfilter.redis.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Autowired
    private RedissonProperties redssionProperties;

    @ConditionalOnProperty(name="redisson.address")
    public RedissonClient redisson(){
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(redssionProperties.getAddress())
                .setDatabase(redssionProperties.getDatabase());
        return Redisson.create(config);
    }
}


