package org.geekbang.time.read.column.others.enums.enummapset;

import java.util.EnumSet;

public class EnumSetDemo1 {

    //定义位域变量
    public static final int TYPE_ONE = 1 << 0 ; //1
    public static final int TYPE_TWO = 1 << 1 ; //2
    public static final int TYPE_THREE = 1 << 2 ; //4
    public static final int TYPE_FOUR = 1 << 3 ; //8

    public static void main(String[] args){
        System.out.println(TYPE_ONE);
        System.out.println(TYPE_TWO);
        System.out.println(TYPE_THREE);
        System.out.println(TYPE_FOUR);
        //位域运算
        int type= TYPE_ONE | TYPE_TWO | TYPE_THREE |TYPE_FOUR;

        EnumSet set =EnumSet.of(Type.TYPE_ONE,Type.TYPE_FOUR);
        System.out.println(set);
    }
}

enum Type{
    TYPE_ONE,TYPE_TWO,TYPE_THREE,TYPE_FOUR
}
