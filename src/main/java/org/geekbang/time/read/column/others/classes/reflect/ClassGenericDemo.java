package org.geekbang.time.read.column.others.classes.reflect;

public class ClassGenericDemo {
    public static void main(String[] args){
        //没有泛型
        Class intClass = int.class;

        //带泛型的Class对象
        //Class integerClass = int.class;
        Class<Integer> integerClass = int.class;

        integerClass = Integer.class;

        //没有泛型的约束,可以随意赋值
        intClass= double.class;

        //编译期错误,无法编译通过
        //integerClass = double.class

        //编译无法通过
        //Class<Integer> numberClass=Integer.class;
        Class<? extends Number> numberClass=Integer.class;
        Class<? extends Number> intClass1 = int.class;
        System.out.println(numberClass==intClass1);
        intClass1 = double.class;

        //编译无法通过
        //Class<? extends Number> someClass = String.class;
    }
}
