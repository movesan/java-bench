package leetcode.dynamicprogramming;

/**
 * @description: 最长回文字符串   5:https://leetcode-cn.com/problems/longest-palindromic-substring/
 * @author: movesan
 * @create: 2020-10-25 09:28
 **/
public class LongestPalindrome {

    /**
     * 动态规划解法
     *
     * @param s
     * @return
     */
    public String longestPalindrome(String s) {
        int n = s.length();
        // s[i] - s[j] 是否为回文
        boolean[][] dp = new boolean[n][n];
        String ans = "";
        // 循环每个子串需要的字符数，如 1个字符，2个字符，3个字符 。。。
        for (int l = 0; l < n; ++l) {
            for (int i = 0; i + l < n; ++i) {
                int j = i + l;
                if (l == 0) {
                    // 如果一个字符，肯定为回文
                    dp[i][j] = true;
                } else if (l == 1) {
                    // 如果两个字符，并且相等时才为回文
                    dp[i][j] = (s.charAt(i) == s.charAt(j));
                } else {
                    // 如果大于两个字符，需要去掉首位两个字符后的字符串为回文，并且首位字符相等，才为回文
                    dp[i][j] = (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]);
                }
                if (dp[i][j] && l + 1 > ans.length()) {
                    ans = s.substring(i, i + l + 1);
                }
            }
        }
        return ans;
    }
}
