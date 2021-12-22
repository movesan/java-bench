package leetcode.dynamicprogramming;

import java.util.Arrays;
import org.junit.Test;

/**
 * @description: 解码方法数 91:https://leetcode-cn.com/problems/decode-ways/
 * @author: movesan
 * @create: 2020-10-21 15:52
 **/
public class NumDecodings {

    /**
     * 递归解法（待验证）
     */
    @Test
    public void numDecodingsTest() {
        System.out.println(numDecodings("124523"));
    }

    private int numDecodings(String s) {
        int len = s.length();
        if (len <= 0) {
            return 0;
        } else if (len == 1) {
            return 1;
        } else {
            return numDecodings(s.substring(0, len - 1 - 2)) + numDecodings(s.substring(0, len - 1 - 1)) + 2;
        }
    }

    /**
     * 动态规划解法
     */
    @Test
    public void numDecodingsDPTest() {
        System.out.println(numDecodingsDP("124523"));
    }

    private int numDecodingsDP(String s) {
        int len = s.length();
        if (len == 0) return 0;
        // dp 状态，dp[i] 表示到第i个字符的解码方法总数
        int[] dp = new int[len + 1];
        dp[0] = 1;
        dp[1] = s.charAt(0) == '0' ? 0 : 1;
        for (int i = 1; i < len; i++) {
            // 如果 i-1 位与 i 位能组成 10-26 之间的数
            if ((s.charAt(i - 1) == '1' || s.charAt(i - 1) == '2') && s.charAt(i) < '7') {
                if (s.charAt(i) == '0') {
                    dp[i + 1] = dp[i - 1];
                } else {
                    dp[i + 1] = dp[i] + dp[i - 1];
                }
            } else if (s.charAt(i) == '0') {
                return 0;
            } else {
                dp[i + 1] = dp[i];
            }
        }
        System.out.println(Arrays.toString(dp));
        return dp[len];
    }
}
