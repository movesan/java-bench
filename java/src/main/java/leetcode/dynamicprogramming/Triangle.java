package leetcode.dynamicprogramming;

import java.util.List;

/**
 * @description: 三角形最小路径和 120:https://leetcode-cn.com/problems/triangle/
 * @author: movesan
 * @create: 2020-10-20 23:28
 **/
public class Triangle {

    /**
     * DP 解法
     *
     * @param triangle
     * @return
     */
    public int minimumTotal(List<List<Integer>> triangle) {
        // status:　dp[i,j] bottom --> dp[i,j] Min{path sum}
        // equation: dp[i,j] = Min{dp[i+1,j], dp[i+1,j+1]} + triangle[i,j]
        int m = triangle.size(); // height
        int n = triangle.get(m - 1).size(); // width
        int[][] dp = new int[m][n];
        for (int i = m - 1; i >= 0; i--) {
            for (int j = 0; j < triangle.get(i).size(); j++) {
                if (i == m - 1) {
                    // base
                    dp[i][j] = triangle.get(i).get(j);
                } else {
                    dp[i][j] = Math.min(dp[i + 1][j], dp[i + 1][j + 1]) + triangle.get(i).get(j);
                }
            }
        }
        return dp[0][0];
    }
}
