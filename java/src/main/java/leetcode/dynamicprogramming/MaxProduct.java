package leetcode.dynamicprogramming;

/**
 * @description: 乘积最大子数组
 * @author: movesan
 * @create: 2020-10-25 23:30
 **/
public class MaxProduct {

    public int maxProduct(int[] nums) {
        // status: dp[i,x] --> 包含第ｉ个元素的子序列最大乘积,x --> 0,1
        // equation: dp[i,2] Max,Min
        int max = nums[0];
        int min = nums[0];
        int res = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int num = nums[i];
            max = max * num;
            min = min * num;
            int maxTemp = max;
            int minTemp = min;
            max = Math.max(Math.max(maxTemp, minTemp), num);
            min = Math.min(Math.min(maxTemp, minTemp), num);
            res = res >= max ? res : max;
        }
        return res;
    }
}
