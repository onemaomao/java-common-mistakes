package org.geekbang.time.totry.design.principle.simpleresponsibility.simple;


public class Course {
    public  void study(String courseName){
        if("直播课".equals(courseName)){
            System.out.println("不能快进");
        }else{
            System.out.println("可以任意的来回播放");
        }
    }
}
