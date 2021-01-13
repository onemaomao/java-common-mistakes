package org.geekbang.time.totry.design.principle.dependencyinversion;


public class DipTest {

    public static void main(String[] args) {
        //=====  V1  ========
//        Tom tom = new Tom();
//        tom.studyJavaCourse();
//        tom.studyPythonCourse();
//        tom.studyAICourse();


        //=====  V2  ========
//        Tom tom = new Tom();
//        tom.study(new JavaCourse());
//        tom.study(new PythonCourse());


        //=====  V3  ========
//        Tom tom = new Tom(new JavaCourse());
//        tom.study();


        //=====  V4  ========
        Tom tom = new Tom();
        tom.setiCourse(new JavaCourse());
        tom.study();
    }
}
