package org.geekbang.time.commonmistakes.collection.aslist;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Slf4j
public class AsListApplication {

    static String a = "A";
    static final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    static class WriteThread extends Thread{

        private String name;

        public WriteThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {

            if("t1".equals(name)){
                log.info("t1 get a1:{}",a);
                rw.writeLock().lock();
                log.info("t1 edit a");

                a = "B";
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rw.writeLock().unlock();
                log.info("t1 get a2:{}",a);
            }
            if("t2".equals(name)){
                log.info("t2 get a1:{}",a);
//                rw.readLock().lock();
                try {
                    Thread.sleep(800L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("t2 get a2:{}",a);
//                rw.readLock().unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        WriteThread t1 = new WriteThread("t1");
        WriteThread t2 = new WriteThread("t2");
        t1.start();
        //Thread.sleep(100L);
        t2.start();

        //right2();

    }

    private static void wrong1() {
        int[] arr = {1, 2, 3};
        List list = Arrays.asList(arr);
        log.info("list:{} size:{} class:{}", list, list.size(), list.get(0).getClass());
    }

    private static void right1() {
        int[] arr1 = {1, 2, 3};
        List list1 = Arrays.stream(arr1).boxed().collect(Collectors.toList());
        log.info("list:{} size:{} class:{}", list1, list1.size(), list1.get(0).getClass());

        Integer[] arr2 = {1, 2, 3};
        List list2 = Arrays.asList(arr2);
        log.info("list:{} size:{} class:{}", list2, list2.size(), list2.get(0).getClass());
    }


    private static void wrong2() {
        String[] arr = {"1", "2", "3"};
        List list = Arrays.asList(arr);
        arr[1] = "4";
        try {
            list.add("5");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info("arr:{} list:{}", Arrays.toString(arr), list);
    }

    private static void right2() {
        String[] arr = {"1", "2", "3"};
        List list = new ArrayList(Arrays.asList(arr));
        arr[1] = "4";
        try {
            list.add("5");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info("arr:{} list:{}", Arrays.toString(arr), list);
    }
}

