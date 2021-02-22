package org.geekbang.time.totry.algorithm.wangzheng;

public class InsertionSort {

    public static void main(String[] args) {
        int[] a = {4,5,1,7,1};
        printArray(a);
        insertionSort(a, 5);
        printArray(a);
    }

    public static void printArray(int[] a){
        for (int i=0; i<a.length; i++){
            System.out.print(a[i]);
            if(i<a.length-1) System.out.print("-");
        }
        System.out.println();
    }

    // 插入排序，a 表示数组，n 表示数组大小
    public static void insertionSort(int[] a, int n) {
        if (n <= 1) return;

        for (int i = 1; i < n; ++i) {
            int value = a[i];
            int j = i - 1;
            // 查找插入的位置
            for (; j >= 0; --j) {
                if (a[j] > value) {
                    a[j+1] = a[j];  // 数据移动
                } else {
                    break;
                }
            }
            a[j+1] = value; // 插入数据
        }
    }
}
