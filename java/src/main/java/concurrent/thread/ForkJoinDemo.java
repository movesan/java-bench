package concurrent.thread;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import static algorithm.sorting.utils.SortingHelper.exch;
import static algorithm.sorting.utils.SortingHelper.less;

/**
 * @description:
 * @author: movesan
 * @create: 2020-08-31 17:42
 **/
public class ForkJoinDemo {

    /**
     * 斐波那契数列
     */
//    @Test
    public void fibonacciTest() {
        //创建分治任务线程池
        ForkJoinPool fjp = new ForkJoinPool(4);
        //创建分治任务
        Fibonacci fib = new Fibonacci(30);
        //启动分治任务
        Integer result = fjp.invoke(fib);
        //输出结果
        System.out.println(result);

    }

    //递归任务
    static class Fibonacci extends RecursiveTask<Integer> {
        final int n;

        Fibonacci(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n <= 1)
                return n;
            Fibonacci f1 = new Fibonacci(n - 1);
            //创建子任务
            f1.fork();
            Fibonacci f2 = new Fibonacci(n - 2);
            //等待子任务结果，并合并结果
            return f2.compute() + f1.join();
        }
    }

    /**
     * 快排
     */
//    @Test
    public static void main(String[] args) {
//        Integer[] arr = {3, 5, 7, 4, 2, 1, 5, 4, 9, 8, 10};
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            Random random = new Random();
            list.add(random.nextInt(10000));
        }
        Integer[] arr = list.toArray(new Integer[0]);
        //创建分治任务线程池
        ForkJoinPool fjp = new ForkJoinPool(1);
        //创建分治任务
        QuickSortTask fib = new QuickSortTask(arr);
        Stopwatch stopwatch = Stopwatch.createStarted();
        //启动分治任务
        fjp.invoke(fib);
        stopwatch.stop();
        System.out.printf("执行时长：%d 毫秒. %n", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        //输出结果
        System.out.println(Arrays.toString(arr));
    }

    static class QuickSortTask extends RecursiveAction {
        Integer[] arr;
        int lo;
        int hi;

        QuickSortTask(Integer[] arr) {
            this.arr = arr;
            this.lo = 0;
            this.hi = arr.length - 1;
        }

        QuickSortTask(Integer[] arr, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected void compute() {
            if (hi <= lo) return;
            int p = partitionRight(arr, lo, hi);
            // 左区间任务
            QuickSortTask quickSortTask1 = new QuickSortTask(arr, lo, p - 1);
            // 右区间任务
            QuickSortTask quickSortTask2 = new QuickSortTask(arr, p + 1, hi);

            invokeAll(quickSortTask1, quickSortTask2);
//            quickSortTask2.fork();
//            quickSortTask1.fork();

            quickSortTask1.join();
            quickSortTask2.join();
        }

        /**
         * 分区函数(选取最右元素为分区点)
         * @param arr
         * @param lo
         * @param hi
         * @return
         */
        private int partitionRight(Comparable[] arr, int lo, int hi) {
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
    }

}
