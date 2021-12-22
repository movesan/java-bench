package algorithm.sorting.quick;

import com.google.common.base.Stopwatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.util.StopWatch;

import static algorithm.sorting.utils.SortingHelper.exch;
import static algorithm.sorting.utils.SortingHelper.less;
import static algorithm.sorting.utils.SortingHelper.show;

/**
 * @description: 快速排序 练习版
 * @author: movesan
 * @create: 2020-04-16 11:03
 **/
public class QuickQuizzes {

    public static void sort(Comparable[] arr) {
        sort(arr, 0, arr.length - 1);
    }

    /**
     * 递归排序
     * @param arr
     * @param lo
     * @param hi
     */
    private static void sort(Comparable[] arr, int lo, int hi) {
        if (hi <= lo) return;
        int p = partitionLeft(arr, lo, hi);
        // 递归左区间
        sort(arr, lo, p - 1);
        // 递归右区间
        sort(arr, p + 1, hi);
    }

    /**
     * 分区函数(选取最左元素为分区点)
     * @param arr
     * @param lo
     * @param hi
     */
    private static int partitionLeft(Comparable[] arr, int lo, int hi) {
        // 取最左边的点为分区点
        Comparable v = arr[lo];
        int i = lo;
        for (int j = lo+1; j <= hi; j++) {
            if (less(arr[j], v)) {
                // 优化点，如果 i==j，可以不进行交换操作
                exch(arr, i+1, j);
                i++;
            }
        }
        exch(arr, lo, i);
        return i;
    }

    /**
     * 分区函数(选取最右元素为分区点)
     * @param arr
     * @param lo
     * @param hi
     * @return
     */
    private static int partitionRight(Comparable[] arr, int lo, int hi) {
        // 取最右边的点为分区点
        Comparable v = arr[hi];
        int i = lo;
        for (int j = lo; j < hi; j++) {
            if (less(arr[j], v)) {
                // 优化点，如果 i==j，可以不进行交互操作
                exch(arr, i, j);
                i++;
            }
        }
        exch(arr, hi, i);
        return i;
    }

    public static void main(String[] args) {
//        Integer[] arr = {3, 5, 7, 4, 2, 1, 5, 4, 9, 8, 10};
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            Random random = new Random();
            list.add(random.nextInt(10000));
        }
        Integer[] arr = list.toArray(new Integer[0]);
        Stopwatch stopwatch = Stopwatch.createStarted();
        QuickQuizzes.sort(arr);
        stopwatch.stop();
        System.out.printf("执行时长：%d 毫秒. %n", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        System.out.println(Arrays.toString(arr));
//        show(arr);
    }
}
