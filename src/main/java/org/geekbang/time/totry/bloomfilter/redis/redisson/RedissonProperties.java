package org.geekbang.time.totry.bloomfilter.redis.redisson;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {

    //很多属性未设置
    //TODO 根据实际情况设置
    private String address;

    private int database;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }
}