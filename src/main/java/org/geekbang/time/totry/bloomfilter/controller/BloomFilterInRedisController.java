package org.geekbang.time.totry.bloomfilter.controller;


import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.geekbang.time.totry.bloomfilter.redis.model.RegisterUser;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@Slf4j
public class BloomFilterInRedisController {

    @Autowired
    private RedissonClient redisson;

    private static RBloomFilter<RegisterUser> filter;


    /**
     * 初始化布隆过滤器
     * 模拟用户注册
     * @return
     */
    @GetMapping("redisson-filter-init")
    public String initBloomFilter(){
        filter = redisson.getBloomFilter("register_users");
        filter.tryInit(10000, 0.003);

        RegisterUser user1 = RegisterUser.builder().userId(1).name("One").build();
        RegisterUser user2 = RegisterUser.builder().userId(2).name("Two").build();
        RegisterUser user3 = RegisterUser.builder().userId(2).name("Three").build();

        filter.add(user1);
        filter.add(user2);
        filter.add(user3);

        long count = filter.count();
        return "filter data input success and filter count=["+count+"]";
    }

    /**
     * 检查请求过来的对象是否存在，避免过多不存在的数据穿透到数据库
     * @return
     */
    @GetMapping("redisson-bloomfilter-exists")
    public String registerUserExists(){
        filter = redisson.getBloomFilter("register_users");
        RegisterUser user1 = RegisterUser.builder().userId(1).name("One").build();
        boolean contains = filter.contains(user1);
        if(contains){
            //存在取明细数据
            log.info("into db get data");
            //有可能数据库中也不存在
        } else {
            //不存在说明真的不存在,走注册流程
            log.info("havent resgister");
        }
        return "bloomfilter "+user1+ " contains="+contains;
    }

    /**
     *
     * @return
     */
    @GetMapping("redisson-bloomfilter-add")
    public String registerUser(){
        filter = redisson.getBloomFilter("register_users");
        RegisterUser user4 = RegisterUser.builder().userId(4).name("Four").build();
        //注册完成之后要往布隆过滤器中放一份数据
        filter.add(user4);

        long count = filter.count();
        return "filter data input "+user4+" success and filter count=["+count+"]";
    }

}
