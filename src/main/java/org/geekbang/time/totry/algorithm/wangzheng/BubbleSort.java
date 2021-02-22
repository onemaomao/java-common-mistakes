package org.geekbang.time.totry.algorithm.wangzheng;

public class BubbleSort {

    public static void main(String[] args) {
        int[] a = {4,5,1,7,1};
        printArray(a);
        bubbleSort(a, 5);
        printArray(a);
    }

    public static void printArray(int[] a){
        for (int i=0; i<a.length; i++){
            System.out.print(a[i]);
            if(i<a.length-1) System.out.print("-");
        }
        System.out.println();
    }


    // 冒泡排序，a 表示数组，n 表示数组大小
    public static void bubbleSort(int[] a, int n) {
        if (n <= 1) return;

        for (int i = 0; i < n; ++i) {
            // 提前退出冒泡循环的标志位
            boolean flag = false;
            for (int j = 0; j < n - i - 1; ++j) {
                if (a[j] > a[j+1]) { // 交换
                    int tmp = a[j];
                    a[j] = a[j+1];
                    a[j+1] = tmp;
                    flag = true;  // 表示有数据交换
                }
            }
            if (!flag) break;  // 没有数据交换，提前退出
        }
    }
}
