package org.geekbang.time.read.column.others.classes.reflect;

import java.lang.reflect.Array;

public class GenericArrayDemo {

    public static void main(String[] args) {
        GenericArrayDemo genericArrayDemo = new GenericArrayDemo();

        String[] a = {"a", "b", "c"};
        String[] min = genericArrayDemo.min(a);
        System.out.println(min);

    }

    /**
     * 接收一个泛型数组，然后创建一个长度与接收的数组长度一样的泛型数组，
     * 并把接收的数组的元素复制到新创建的数组中，
     * 最后找出新数组中的最小元素，并打印出来
     * @param a
     * @param <T>
     */
    public  <T extends Comparable<T>> T[] min(T[] a) {
        //通过反射创建相同类型的数组
        T[] b = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length);
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i];
        }
        T min = null;
        boolean flag = true;
        for (int i = 0; i < b.length; i++) {
            if (flag) {
                min = b[i];
                flag = false;
            }
            if (b[i].compareTo(min) < 0) {
                min = b[i];
            }
        }
        System.out.println(min);
        return b;
    }
}
