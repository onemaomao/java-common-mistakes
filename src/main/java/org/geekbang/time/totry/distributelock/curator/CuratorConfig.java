package org.geekbang.time.totry.distributelock.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CuratorConfig {

    private static String CONNECTION_STR="192.168.33.39:2181,192.168.33.40:2182,192.168.33.42:2183";

    @Bean(initMethod = "start")
    public CuratorFramework curatorFramework(){
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString(CONNECTION_STR).sessionTimeoutMs(50000000).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        //curatorFramework.start();//config initMethod and delete here
        return curatorFramework;
    }
}
