package leetcode.binarysearch;

import org.junit.Test;

/**
 * @description: 实现求解平方根的函数
 * @author: movesan
 * @create: 2020-09-17 15:59
 **/
public class SquareRoot {

    public int sqrt(int x) {
        if (x == 0 || x == 1) return x;

        int lo = 0;
        int hi = x;
        int res = 0;
        while (lo <= hi) {
            int mid = lo + ((hi - lo) >> 1);
            if (mid == x / mid) {
                return mid;
            } else if (mid > x / mid) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
                res = mid;
            }
        }
        return res;
    }

    @Test
    public void test() {
        System.out.println(sqrt(9));
    }
}
