package org.geekbang.time.totry.coroutine;


import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;

/**
 * Quasar示例
 */
public class Demo001 {

    public static void main(String[] args) {
        new Fiber<Void>("Caller", new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                m1();
            }
        }).start();
    }

    static void m1() throws SuspendExecution, InterruptedException {
        String m = "m1";
        System.out.println("m1 begin.");
        m = m2();
        m = m3();
        System.out.println("m1 end");
        System.out.println(m);
    }
    static String m2() throws SuspendExecution, InterruptedException {
        return "m2";
    }
    static String m3() throws SuspendExecution, InterruptedException {
        return "m3";
    }


}
