package org.geekbang.time.totry.multithreading.pattern.GuardedSuspension;

import java.util.Random;

/**
 * 服务端线程不断从请求队列中获取请求，然后处理请求
 */
public class ServerThread extends  Thread{

    private Random random;

    private RequestQueue requestQueue;

    public ServerThread(RequestQueue requestQueue,String name, long seed) {
        super(name);
        this.requestQueue = requestQueue;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            Request request = null;
            try {
                request = requestQueue.getRequest();
                System.out.println(Thread.currentThread().getName() + " handles " + request);
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
