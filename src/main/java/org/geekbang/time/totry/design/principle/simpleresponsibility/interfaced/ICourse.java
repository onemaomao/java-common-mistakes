package org.geekbang.time.totry.design.principle.simpleresponsibility.interfaced;


public interface ICourse {

    String getCourseName();
    byte[] getCourseVideo();

    void studyCourse();
    void refundCourse();
}
