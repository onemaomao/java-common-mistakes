package org.geekbang.time.totry.multithreading.producerconsumer;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerWithLockDemo {

    private LinkedList<String> product = new LinkedList<String>(); //商品队列

    private int maxInventory = 10; // 最大库存

    private Lock lock = new ReentrantLock();// 资源锁

    private Condition condition = lock.newCondition();// 库存非满和非空条件

    /**
     * 生产-新增商品库存
     *
     * @param e
     */
    public void produce(String e) {
        lock.lock();
        try {
            while (product.size() == maxInventory) {
                condition.await();
            }
            product.add(e);
            System.out.println(" 放入一个商品库存，总库存为：" + product.size());
            condition.signalAll();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    /**
     * 消费
     *
     * @return
     */
    public String consume() {
        String result = null;
        lock.lock();
        try {
            while (product.isEmpty()) {
                condition.await();
            }
            result = product.removeLast();
            System.out.println(" 消费一个商品，总库存为：" + product.size());
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生产者
     */
    private class Producer implements Runnable {

        @Override
        public void run() {
            for (int i=0; i<20; i++){
                produce("商品 "+i);
            }
        }
    }

    private class Consumer implements Runnable{

        @Override
        public void run() {
            for (int i=0; i<20; i++){
                consume();
            }
        }
    }

    public static void main(String[] args) {
        ProducerConsumerWithLockDemo pc = new ProducerConsumerWithLockDemo();
        new Thread(pc.new Producer()).start();
        new Thread(pc.new Consumer()).start();
        new Thread(pc.new Producer()).start();
        new Thread(pc.new Consumer()).start();
    }
}