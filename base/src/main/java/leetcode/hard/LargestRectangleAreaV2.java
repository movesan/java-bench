package leetcode.hard;

import java.util.Stack;
import org.junit.Test;

/**
 * @description: 最大矩形  85:https://leetcode-cn.com/problems/maximal-rectangle/
 * @author: movesan
 * @create: 2020-10-24 15:09
 **/
public class LargestRectangleAreaV2 {


    /**
     * 遍历解法 - 单调栈
     */
    @Test
    public void maximalRectangleTest() {
        /*
          ["1","0","1","0","0"],
          ["1","0","1","1","1"],
          ["1","1","1","1","1"],
          ["1","0","0","1","0"]
         */
        char[][] param = new char[][]{
                {'1', '0', '1', '0', '0'},
                {'1', '0', '1', '1', '1'},
                {'1', '1', '1', '1', '1'},
                {'1', '0', '0', '1', '0'}
        };
        System.out.println(maximalRectangle(param));
    }

    public int maximalRectangle(char[][] matrix) {
        if (matrix.length == 0) {
            return 0;
        }
        int[] heights = new int[matrix[0].length];
        int maxArea = 0;
        for (char[] chars : matrix) {
            //遍历每一列，更新高度
            for (int col = 0; col < matrix[0].length; col++) {
                if (chars[col] == '1') {
                    heights[col] += 1;
                } else {
                    heights[col] = 0;
                }
            }
            //调用上一题的解法，更新函数
            maxArea = Math.max(maxArea, largestRectangleStack(heights));
        }
        return maxArea;
    }

    /**
     * 计算到每一层时的最大面积
     *
     * @param heights
     * @return
     */
    public int largestRectangleStack(int[] heights) {
        int n = heights.length;
        // 每个位置的高度
        Stack<Integer> stack = new Stack<Integer>();

        // 计算每个位置的左边最近的一个小于当前位置的下标
        int[] left = new int[n];
        for (int i = 0; i < n; i++) {
            // 如果栈中的高度比当前大，则要pop出去，这样可以留住小于当前高度的位置
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
                stack.pop();
            }
            left[i] = (stack.isEmpty() ? -1 : stack.peek());
            stack.push(i);
        }

        // 计算每个位置的右边最近的一个小于当前位置的下标
        stack.clear();
        int[] right = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            // 同上
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
                stack.pop();
            }
            right[i] = (stack.isEmpty() ? n : stack.peek());
            stack.push(i);
        }

        int ans = 0;
        for (int i = 0; i < n; i++) {
            // 计算每个位置的面积
            ans = Math.max(ans, (right[i] - left[i] - 1) * heights[i]);
        }
        return ans;
    }

    /**
     * 动态规划解法 TODO
     */
    public void largestRectangleDPTest() {


    }

}
