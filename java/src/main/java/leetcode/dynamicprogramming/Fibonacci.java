package leetcode.dynamicprogramming;

import org.junit.Test;

/**
 * @description: 斐波那契数列
 * @author: movesan
 * @create: 2020-10-20 22:12
 **/
public class Fibonacci {

    /**
     * 递归解法
     */
    @Test
    public void fibonacciRecursion() {
        System.out.println(fib(7));
    }

    private int fib(int n) {
        if (n <= 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        } else {
            return fib(n - 1) + fib(n - 2);
        }
    }

    /**
     * 递归解法（带有剪枝）
     */
    @Test
    public void fibonacciRecursionOptimize() {
        int n = 7;
        int[] memo = new int[n + 1];
        System.out.println(fib(n, memo));
    }

    private int fib(int n, int[] memo) {
        if (n <= 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        } else if (memo[n] == 0) {
            memo[n] = fib(n - 1) + fib(n - 2);
        }
        return memo[n];
    }

    /**
     * 动态规划（自底向上）
     */
    @Test
    public void fibonacciDynamic() {
        System.out.println(fibDynamic(7));
    }

    private int fibDynamic(int n) {
        int[] fib = new int[n + 1];
        fib[0] = 0;
        fib[1] = 1;
        for (int i = 2; i <= n; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
        return fib[n];
    }
}
