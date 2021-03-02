package org.geekbang.time.read.column.others.classes.reflect;

public class SweetShop {
    public static void print(Object obj) {
        System.out.println(obj);
    }
    public static void main(String[] args) {
        print("inside main");
        new Candy();
        print("After creating Candy");
        try {
            Class.forName("org.geekbang.time.read.column.others.classes.reflect.Gum");
        } catch(ClassNotFoundException e) {
            print("Couldn't find Gum");
        }
        print("After Class.forName(\"org.geekbang.time.read.column.others.classes.reflect.Gum\")");
        new Cookie();
        print("After creating Cookie");
    }
}
class Candy {
    static {   System.out.println("Loading Candy"); }
}

class Gum {
    static {   System.out.println("Loading Gum"); }
}

class Cookie {
    static {   System.out.println("Loading Cookie"); }
}
