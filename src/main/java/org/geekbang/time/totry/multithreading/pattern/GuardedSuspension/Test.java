package org.geekbang.time.totry.multithreading.pattern.GuardedSuspension;

public class Test {

    public static void main(String[] args) {
        RequestQueue requestQueue = new RequestQueue();
        new ClientThread(requestQueue,"Alice", 324592L).start();
        new ServerThread(requestQueue, "Bobby", 653897L).start();
    }
}
