package org.geekbang.time.totry.test;

public class JVMFlow {
/*

    private static int i=1;
    static{
        i=0;
    }
    public static void main(String [] args){
        System.out.println(i);
    }
*/
    static{
        i=0;
    }
    private static int i=1;
    public static void main(String [] args){
        System.out.println(i);
    }


}
