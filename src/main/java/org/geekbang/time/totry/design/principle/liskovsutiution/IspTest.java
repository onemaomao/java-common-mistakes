package org.geekbang.time.totry.design.principle.liskovsutiution;




public class IspTest {

    public static void resize(Rectangle rectangle){
        while (rectangle.getWidth() >= rectangle.getHeight()){
            rectangle.setHeight(rectangle.getHeight() + 1);
            System.out.println("Width:" +rectangle.getWidth() +",Height:" + rectangle.getHeight());
        }
        System.out.println("Resize End,Width:" +rectangle.getWidth() +",Height:" + rectangle.getHeight());
    }

//    public static void main(String[] args) {
//        Rectangle rectangle = new Rectangle();
//        rectangle.setWidth(20);
//        rectangle.setHeight(10);
//        resize(rectangle);
//    }

//    public static void main(String[] args) {
//        com.gupaoedu.vip.design.principle.liskovsutiution.simple.Square square = new Square();
//        square.setLength(10);
//        resize(square);
//    }

}
