package leetcode.dynamicprogramming;

import org.junit.Test;

/**
 * @description: 最长上升子序列
 * @author: movesan
 * @create: 2020-10-25 23:37
 **/
public class LengthOfLIS {

    public int lengthOfLIS(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        // 定义 dp[i] 为考虑前 i 个元素，以第 i 个数字结尾的最长上升子序列的长度，注意 nums[i] 必须被选取
        int[] dp = new int[nums.length];
        dp[0] = 1;
        int res = 1;
        for (int i = 1; i < nums.length; i++) {
            int max = 0;
            for (int j = 0; j < i; j++) {
                // 保证比之前所有数都大
                if (nums[j] < nums[i]) {
                    max = Math.max(max, dp[j]);
                }
            }
            dp[i] = max + 1;
            res = Math.max(res, dp[i]);
        }
        return res;
    }

    @Test
    public void test() {
        int[] nums = new int[]{2, 1, 3, 4, 1, 2, 1, 5, 4};


        System.out.println(lengthOfLIS(nums));
    }
}
