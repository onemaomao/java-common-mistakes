package org.geekbang.time.totry.design.principle.principle.compositereuse;


public class MySQLConnection extends DBConnection {
    public String getConnection() {
        return "获取MySQL数据连接";
    }
}
