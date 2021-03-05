package org.geekbang.time.read.column.others.enums;

public enum EnumDemo3 {
    FIRST{
        @Override
        public String getInfo() {
            return "FIRST TIME";
        }
    },
    SECOND{
        @Override
        public String getInfo() {
            return "SECOND TIME";
        }
    };

    /**
     * 定义抽象方法
     * @return
     */
    public abstract String getInfo();

    public static void main(String[] args) {
        System.out.println("F:"+EnumDemo3.FIRST.getInfo());
        System.out.println("S:"+EnumDemo3.SECOND.getInfo());
    }

}
