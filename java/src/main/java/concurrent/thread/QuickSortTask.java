package concurrent.thread;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: movesan
 * @create: 2020-09-01 11:38
 **/
public class QuickSortTask extends RecursiveAction {

    private Integer[] array;
    private int left;
    private int right;

    public QuickSortTask(Integer[] array, int left, int right) {
        this.array = array;
        this.left = left;
        this.right = right;
    }

    @Override
    protected void compute() {
        int pivot = partition(array, left, right);
        QuickSortTask task1 = null;
        QuickSortTask task2 = null;
        if (pivot - left > 1) {
            task1 = new QuickSortTask(array, left, pivot-1);
            task1.fork();
        }
        if (right - pivot > 1) {
            task2 = new QuickSortTask(array, pivot+1, right);
            task2.fork();
        }
        if (task1 != null && !task1.isDone()) {
            task1.join();
        }
        if (task2 != null && !task2.isDone()) {
            task2.join();
        }
    }

    public static int partition(Integer[] a, int left, int right) {
        int pivot = a[left];
        while (left < right) {
            while (left < right && a[right] >= pivot) {
                right--;
            }
            swap(a, left, right);
            while (left < right && a[left] <= pivot) {
                left++;
            }
            swap(a, left, right);
        }
        return left;
    }

    public static void swap(Integer[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void main(String[] args) {
//        int[] a = {4,2,1,4,7,5,3,8,2,7,1,78,89,6,5,4,8,5};
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            Random random = new Random();
            list.add(random.nextInt(10000));
        }
        Integer[] a = list.toArray(new Integer[0]);
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        QuickSortTask task = new QuickSortTask(a, 0, a.length-1);
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Void> result = forkJoinPool.submit(task);
        try {
            result.get();
            stopwatch.stop();
            System.out.printf("执行时长：%d 毫秒. %n", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            //输出结果
            System.out.println(Arrays.toString(a));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
