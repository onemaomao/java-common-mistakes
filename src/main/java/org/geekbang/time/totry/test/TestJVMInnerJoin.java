package org.geekbang.time.totry.test;

/**
 * 测试方法内联
 */
public class TestJVMInnerJoin {
    private static int add1(int x1, int x2, int x3, int x4) {
        return x1 + x2+ x3 + x4;
    }
    public static void main(String[] args) {
        for(int i=0; i<1000000; i++) {// 方法调用计数器的默认阈值在 C1 模式下是 1500 次，在 C2 模式在是 10000 次，我们循环遍历超过需要阈值
            add1(1,2,3,4);
        }
    }
    //-XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
}
