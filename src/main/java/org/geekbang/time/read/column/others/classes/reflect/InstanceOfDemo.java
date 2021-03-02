package org.geekbang.time.read.column.others.classes.reflect;

public class InstanceOfDemo {

    public static void main(String[] args) {
        test(new A());
        test(new B());
    }

    static void test(Object x) {
        print("Testing x of type " + x.getClass());
        print("x instanceof A " + (x instanceof A));
        print("x instanceof B "+ (x instanceof B));
        print("A.isInstance(x) "+ A.class.isInstance(x));
        print("B.isInstance(x) " +
                B.class.isInstance(x));
        print("x.getClass() == A.class " +
                (x.getClass() == A.class));
        print("x.getClass() == B.class " +
                (x.getClass() == B.class));
        print("x.getClass().equals(A.class)) "+
                (x.getClass().equals(A.class)));
        print("x.getClass().equals(B.class)) " +
                (x.getClass().equals(B.class)));
    }

    public static void print(Object o){
        System.out.println(o);
    }
}

class A {}

class B extends A {}
