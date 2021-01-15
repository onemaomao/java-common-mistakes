package org.geekbang.time.totry.bloomfilter.controller;


import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;

@RestController
@Slf4j
public class BloomFilterInGuavaController {

    private static BloomFilter<Integer> bf;

    /**
     * init some data
     */
    static {
        int count = 100;
        bf = BloomFilter.create(Funnels.integerFunnel(), count);
        for (int i=0; i<count; i++){
            bf.put(i);
        }
    }

    /**
     * 测试guava bloomfilter功能
     * @param number
     * @return
     */
    @GetMapping("guava-bf01")
    public String guavaBloomFilterUseAge(@RequestParam(value = "number", defaultValue = "99999999999") int number){

        if(!bf.mightContain(number)){
            String notExists = "["+number+"] 这个数字确实没有";
            log.info(notExists);
            return notExists;
        }

        if(bf.mightContain(number)){
            String exists = "["+number+"] 这个数字在缓存中";
            log.info(exists);
            return exists;
        }
        return "";
    }


    /**
     * 以下用于测试出错的概率
     * 以及对比hash的效率(仅仅是检索时间,不考虑空间)
     */
    private static BloomFilter<Integer> bfProb;
    private static HashMap<Integer,Object> map = new HashMap<>();
    static {
        int count = 1000000;
        //bfProb = BloomFilter.create(Funnels.integerFunnel(), count);
        bfProb = BloomFilter.create(Funnels.integerFunnel(), count, 0.00000001f);

        for (int i=0; i<count; i++){
            bfProb.put(i);
        }

        //对比Hash

        final Object o = new Object();
        for (int i=0; i<count; i++){
            map.put(i,o);
        }
    }
    @GetMapping("guava-bf02")
    public String guavaBloomFilterProbability(@RequestParam(value = "numCounts", defaultValue = "1000000") int numCounts){

        int count1 = 0;
        for (int i = 0; i < numCounts; i++) {
            if (!bfProb.mightContain(i)) {
                log.info("["+i+"]"+" 逃了");
                count1 ++;
            }
        }
        log.info("逃脱的数量:{}",count1);

        int count2 = 0;
        for (int i = numCounts; i < numCounts + 10000; i++) {
            if (bfProb.mightContain(i)) {
                log.info("["+i+"]"+" 误伤了");
                count2++;
            }
        }
        log.info("误伤的数量:{}",count2);
        return "本次测试逃脱了:["+count1+"], 误伤了:["+count2+"]";
    }

    @GetMapping("guava-vs-hash")
    public String guavaBloomFilterVsHash(@RequestParam(value = "numCounts", defaultValue = "1000000") int numCounts){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("guava bloomfilter");
        for (int i = 0; i < numCounts; i++) {
            bfProb.mightContain(i);
        }
        stopWatch.stop();
        stopWatch.start("hash");
        for (int i = 0; i < numCounts; i++) {
            map.get(i);
        }
        stopWatch.stop();
        String s = stopWatch.prettyPrint();
        return s;
    }

}
