package org.geekbang.time.read.column.test;

public class ExceptionTableDemo {

    public static void main(String[] args) {
        try {
            int a = 0;
            System.out.println(a);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
