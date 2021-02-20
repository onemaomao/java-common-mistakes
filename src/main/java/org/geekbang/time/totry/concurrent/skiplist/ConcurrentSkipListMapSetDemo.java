package org.geekbang.time.totry.concurrent.skiplist;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * skipList示例
 */
public class ConcurrentSkipListMapSetDemo {

    public static void main(String[] args) {
        ConcurrentSkipListMapDemo();

        ConcurrentSkipListDemo();

    }

    private static void ConcurrentSkipListDemo() {
        ConcurrentSkipListSet<String> cslS = new ConcurrentSkipListSet<>();
        cslS.add("2017-05-23 17:18:08_key1");
        cslS.add("2017-05-22 16:18:10_key1");
        cslS.add("2017-05-22 16:18:08_key2");
        cslS.add("2013-05-22 16:18:08_key2");
//        System.out.println(cslS);

        Iterator<String> iterator = cslS.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            System.out.println(next);
        }
    }

    private static void ConcurrentSkipListMapDemo() {
        ConcurrentSkipListMap<String,Integer> cslMap = new ConcurrentSkipListMap<String,Integer>();
        cslMap.put("2017-05-22 16:18:10_key1", 1);
        cslMap.put("2017-05-22 16:18:08_key2", 2);
        cslMap.put("2017-05-22 16:18:20_key3", 1);
        cslMap.put("2017-05-22 16:18:18_key4", 2);
        cslMap.put("2017-05-22 16:18:30_key5", 1);
        cslMap.put("2017-05-22 16:18:28_key2", 2);
        cslMap.put("2017-05-22 16:18:40_key2", 1);
        cslMap.put("2017-05-22 16:18:38_key1", 2);
        cslMap.put("2017-05-22 16:18:59_key1", 2);
        cslMap.put("2017-05-22 17:18:10_key1", 2);
        cslMap.put("2017-05-22 17:18:08_key1", 2);
        cslMap.put("2017-05-23 17:18:08_key1", 2);

        for (Map.Entry<String, Integer> entry : cslMap.entrySet()) {
            System.out.println("key:" + entry.getKey() + " value:" + entry.getValue());
        }

/*

        String startKey = "2017-05-22 16:18:08";
        String endKey = "2017-05-22 16:18:60";
        ConcurrentNavigableMap<String, Integer> subMap = cslMap.subMap(startKey, endKey); //前闭后开
        for (Map.Entry<String, Integer> entry : subMap.entrySet()) { //取一定范围的集合
            System.out.println("key:" + entry.getKey() + " value:" + entry.getValue());
        }
*/
    }
}
