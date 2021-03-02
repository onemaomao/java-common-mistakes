package org.geekbang.time.read.column.others.classes.reflect;

public class ClassForNameDemo {
    public static void main(String[] args) {
        Class clazz = null;
        try{
            //通过Class.forName获取Gum类的Class对象
            clazz=Class.forName("org.geekbang.time.read.column.others.classes.reflect.Gum");
            System.out.println("forName=clazz:"+clazz.getName());
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        //通过实例对象获取Gum的Class对象
        Gum gum = new Gum();
        Class clazz2=gum.getClass();
        System.out.println("new=clazz2:"+clazz2.getName());
        System.out.println(clazz==clazz2);
        Class clazz3=Gum.class;
        System.out.println(clazz==clazz3);


    }
}
