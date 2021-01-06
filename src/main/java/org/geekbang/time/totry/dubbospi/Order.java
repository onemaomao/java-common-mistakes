package org.geekbang.time.totry.dubbospi;

import org.apache.dubbo.common.extension.SPI;

/**
 * 下单接口
 */
@SPI("wechat")
public interface Order {
    // 订单支持方式
    String way();
}
