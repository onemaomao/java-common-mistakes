package org.geekbang.time.totry.algorithm.wangzheng;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapDemo {
    public static void main(String[] args) {
        //demo01();

        demo02();
    }

    private static void demo02() {
        // 10 是初始大小，0.75 是装载因子，true 是表示按照访问时间排序
        HashMap<Integer, Integer> m = new LinkedHashMap<>(10, 0.75f, true);
        m.put(3, 11);
        m.put(1, 12);
        m.put(5, 23);
        m.put(2, 22);

        m.put(3, 26);
        m.get(5);

        for (Map.Entry e : m.entrySet()) {
            System.out.println(e.getKey());
        }
    }

    private static void demo01() {
        HashMap<Integer, Integer> m = new LinkedHashMap<>();
        m.put(3, 11);
        m.put(1, 12);
        m.put(5, 23);
        m.put(2, 22);


        for (Map.Entry e : m.entrySet()) {
            System.out.println(e.getKey());
        }
    }
}
