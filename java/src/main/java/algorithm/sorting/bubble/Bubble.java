package algorithm.sorting.bubble;

import static algorithm.sorting.utils.SortingHelper.exch;
import static algorithm.sorting.utils.SortingHelper.less;
import static algorithm.sorting.utils.SortingHelper.show;

/**
 * @description: 冒泡排序
 * @author: movesan
 * @create: 2020-01-12 14:15
 **/
public class Bubble {

    // This class should not be instantiated.
    private Bubble() { }

    // 冒泡排序，a表示数组，n表示数组大小
    public static void sort(Comparable[] a) {
        int n = a.length;
        if (n <= 1) return;

        for (int i = 0; i < n; ++i) {
            // 提前退出冒泡循环的标志位
            boolean flag = false;
            for (int j = 0; j < n - i - 1; ++j) {
                if (less(a[j+1], a[j])) { // 交换
                    exch(a, j, j+1);
                    flag = true;  // 表示有数据交换
                }
            }
            if (!flag) break;  // 没有数据交换，提前退出
        }
    }

    public static void main(String[] args) {
//        String[] a = StdIn.readAllStrings();
        String[] a = {"S", "O", "R", "T", "E", "X", "A", "M", "P", "L", "E"};
        Bubble.sort(a);
        show(a);
    }
}
