package org.geekbang.time.tutorialspoint.java.collections.classes;
import java.util.*;

public class WeakHashMapDemo {
    private static Map map;
    public static void main (String args[]) {
        map = new WeakHashMap();
        map.put(new String("Maine"), "Augusta");

        System.out.println("------");
        System.out.println(map.get("Maine"));
        System.out.println("------");
        Runnable runner = new Runnable() {
            public void run() {
                while (map.containsKey("Maine")) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {
                    }
                    System.out.println("Thread waiting");
                    System.gc();
                }
            }
        };
        Thread t = new Thread(runner);
        t.start();
        System.out.println("Main waiting");
        try {
            t.join();
        } catch (InterruptedException ignored) {
        }
        System.out.println("------");
        System.out.println(map.get("Maine"));
        System.out.println("------");

    }
}
