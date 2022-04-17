package leetcode;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/4/12 10:50
 * @version 1.0
 */
public class StockIssue {

    /** =============  买卖股票最佳时机 I 121 =============  **/
    /**
     * 找出最低价格买入
     * @param prices
     * @return
     */
    public int maxProfit(int[] prices) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] < min) {
                min = prices[i];
                continue;
            }
            max = Math.max(max, prices[i] - min);
        }
        return max;
    }

    /** =============  买卖股票最佳时机 II 122 =============  **/
    public int maxProfitDP(int[] prices) {
        int len = prices.length;
        if (len < 2) {
            return 0;
        }
        // 0：持有现金
        // 1：持有股票
        // 状态转移：0 → 1 → 0 → 1 → 0 → 1 → 0
        int[][] dp = new int[len][2];
        dp[0][0] = 0;
        dp[0][1] = -prices[0];
        for (int i = 1; i < len; i++) {
            // 这两行调换顺序也是可以的
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1] + prices[i]);
            dp[i][1] = Math.max(dp[i - 1][1], dp[i - 1][0] - prices[i]);
        }
        return dp[len - 1][0];
    }

}