package org.geekbang.time.read.column.others.classes.reflect;

public class TypeCastDemo {
    public static void main(String[] args) {
        //Animal animal= new Dog();
        //强制转换
        //Dog dog = (Dog) animal;

        Animal animal= new Dog();
        //这两句等同于Dog dog = (Dog) animal;
        Class<Dog> dogType = Dog.class;
        Dog dog = dogType.cast(animal);
        System.out.println(1);

    }
}

