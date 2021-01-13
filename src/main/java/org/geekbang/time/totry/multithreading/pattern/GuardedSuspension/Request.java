package org.geekbang.time.totry.multithreading.pattern.GuardedSuspension;

import java.util.LinkedList;

/**
 * 请求
 */
public class Request {

    private final String name;

    public Request(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Request{" +
                "name='" + name + '\'' +
                '}';
    }
}

/**
 * 请求队列
 */
class RequestQueue{
    private final LinkedList<Request> queue = new LinkedList<>();

    public synchronized Request getRequest() throws InterruptedException {
        while (queue.size() <= 0){
            wait();
        }
        return queue.removeFirst();
    }

    public synchronized void putRequest(Request request){
        queue.addLast(request);
        notifyAll();
    }
}
