package org.geekbang.time.totry.functional.java.basic.test01;

import org.junit.Test;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * lambda示例
 */
public class LambdaDemo01 {

    @Test
    public void test1(){
        Comparator<String> com = new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(o1.length(), o2.length());
            }
        };

        TreeSet<String> ts = new TreeSet<>(com);

        TreeSet<String> ts2 = new TreeSet<>(new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(o1.length(), o2.length());
            }

        });
    }
}
