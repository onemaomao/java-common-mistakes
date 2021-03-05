package org.geekbang.time.read.column.others.classloader;

public class FileClassLoader extends  ClassLoader{

    private String rootDir;

    public FileClassLoader(String rootDir) {
        this.rootDir = rootDir;
    }

    public static void main(String[] args) throws ClassNotFoundException {
//        FileClassLoader loader1 = new FileClassLoader("");
//
//        System.out.println("自定义类加载器的父加载器: "+loader1.getParent());
//        System.out.println("系统默认的AppClassLoader: "+ClassLoader.getSystemClassLoader());
//        System.out.println("AppClassLoader的父类加载器: "+ClassLoader.getSystemClassLoader().getParent());
//        System.out.println("ExtClassLoader的父类加载器: "+ClassLoader.getSystemClassLoader().getParent().getParent());


        String rootDir="";
        //创建两个不同的自定义类加载器实例
        FileClassLoader loader1 = new FileClassLoader(rootDir);
        FileClassLoader loader2 = new FileClassLoader(rootDir);
        //通过findClass创建类的Class对象
        Class<?> object1=loader1.findClass("org.geekbang.time.read.column.others.classloader.DemoObj");
        Class<?> object2=loader2.findClass("org.geekbang.time.read.column.others.classloader.DemoObj");

        System.out.println("findClass->obj1:"+object1.hashCode());
        System.out.println("findClass->obj2:"+object2.hashCode());
    }
}
