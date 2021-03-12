package org.geekbang.time.totry.distributelock.controller;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.geekbang.time.totry.distributelock.curator.InterProcessCloseableMutex;
import org.geekbang.time.totry.distributelock.redis.RedisLock;
import org.geekbang.time.totry.distributelock.zk.ZkLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class DistributeLockController {
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("redislock")
    public String redisLock(){
        log.info(Thread.currentThread().getName()+" enter redis lock");
        try (RedisLock redisLock = new RedisLock(redisTemplate,"redisKey",30)){
            if (redisLock.getLock()) {
                log.info(Thread.currentThread().getName()+" lock success");
                Thread.sleep(15000);
            } else {
                log.info(Thread.currentThread().getName()+" redisLock fail");
                return Thread.currentThread().getName()+" redisLock fail";
            }
        } catch (Exception e) {
            log.error(Thread.currentThread().getName()+" redisLock invoke exception:", e);
        }
        log.info(Thread.currentThread().getName()+" redislock invoke success");
        return Thread.currentThread().getName() + "redislock invoke success";
    }

    @RequestMapping("zklock")
    public String zkLock(){
        log.info(Thread.currentThread().getName()+" enter zk lock");
        try (ZkLock zkLock = new ZkLock("192.168.33.39:2181,192.168.33.40:2182,192.168.33.42:2183","orderlock")){
            if (zkLock.getLock()) {
                log.info(Thread.currentThread().getName()+" lock success");
                Thread.sleep(15000);
            }
        } catch (Exception e) {
            log.error(Thread.currentThread().getName()+" zk lock invoke exception:", e);
        }
        log.info(Thread.currentThread().getName()+" zklock invoke success");
        return Thread.currentThread().getName() + " zklock invoke success";
    }



    @Autowired
    private CuratorFramework curatorFramework;

    @RequestMapping("curatorlock")
    public String curatorLock(){

//        final InterProcessMutex lock=new InterProcessMutex(curatorFramework,"/locks");


        log.info(Thread.currentThread().getName()+" enter curatorlock");

        /**
         * try with resource方式
         */
        try (InterProcessCloseableMutex lock=new InterProcessCloseableMutex(curatorFramework,"/locks")) {
            log.info(Thread.currentThread().getName()+" curatorlock success");
            lock.acquire();
            Thread.sleep(15000);
        } catch (Exception e){
            log.error("error:", e);
        }

        //传统方式
//        try {
//            lock.acquire();
//            Thread.sleep(15000);
//            log.info(Thread.currentThread().getName()+" curatorlock success");
//        } catch (Exception e) {
//            log.error("error1:", e);
//        } finally {
//            try {
//                lock.release();
//                log.info(Thread.currentThread().getName()+" curator release success");
//            } catch (Exception e) {
//                log.error("error2:", e);
//            }
//        }
        log.info(Thread.currentThread().getName()+" curatorlock invoke success");
        return Thread.currentThread().getName() + " curatorlock invoke success";
    }


    @Autowired
    private RedissonClient redisson;

    @RequestMapping("redissonlock")
    public String redissonLock(){
        RLock lock = redisson.getLock("order");
        try {
            lock.lock();
            log.info(Thread.currentThread().getName()+" lock success");
            Thread.sleep(15000);
        } catch (Exception e) {
            log.error(Thread.currentThread().getName()+" redissonlock invoke exception:", e);
        } finally {
            lock.unlock();
            log.info(Thread.currentThread().getName()+" redissonlock unlock success");
        }
        log.info(Thread.currentThread().getName()+" redissonlock invoke success");
        return Thread.currentThread().getName() + " redissonlock invoke success";
    }

    @RequestMapping("redlock")
    public String redLock(){
        RLock rLock = redisson.getLock("b");
        RedissonRedLock lock = new RedissonRedLock(rLock);
        //lock.lock();
        lock.lock(3, TimeUnit.MINUTES);

        lock.unlock();
        return Thread.currentThread().getName() + " redlock invoke success";
    }
}
