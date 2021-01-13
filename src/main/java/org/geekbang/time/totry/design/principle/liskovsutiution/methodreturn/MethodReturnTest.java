package org.geekbang.time.totry.design.principle.liskovsutiution.methodreturn;


public class MethodReturnTest {
    public static void main(String[] args) {
        Base child = new Child();
        System.out.println(child.method());
    }
}
