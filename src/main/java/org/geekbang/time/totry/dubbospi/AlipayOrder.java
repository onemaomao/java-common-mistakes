package org.geekbang.time.totry.dubbospi;

public class AlipayOrder implements Order {
    @Override
    public String way() {
        System.out.println("--- 支付宝way() ---");
        return "支付宝支付方式";
    }
}
