package leetcode.hard;

import java.util.Arrays;
import java.util.Stack;
import org.junit.Test;

/**
 * @description: 最大矩形  84:https://leetcode-cn.com/problems/largest-rectangle-in-histogram/
 * @author: movesan
 * @create: 2020-10-24 15:09
 **/
public class LargestRectangleArea {

    /**
     * 枚举法（枚举高）
     */
    @Test
    public void largestRectangleEnumerateTest() {
        System.out.println(largestRectangleEnumerate(new int[]{2, 1, 5, 6, 2, 3}));
    }

    public int largestRectangleEnumerate(int[] heights) {
        int n = heights.length;
        int ans = 0;
        for (int mid = 0; mid < n; mid++) {
            // 枚举高
            int height = heights[mid];
            int left = mid;
            int right = mid;
            // 确定左右边界
            while (left - 1 >= 0 && heights[left - 1] >= height) {
                --left;
            }
            while (right + 1 < n && heights[right + 1] >= height) {
                ++right;
            }
            // 计算面积
            ans = Math.max(ans, (right - left + 1) * height);
        }
        return ans;

    }

    /**
     * 单调栈
     */
    @Test
    public void largestRectangleStackTest() {
        System.out.println(largestRectangleStack(new int[]{2, 1, 5, 6, 2, 3}));
    }

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
     * 单调栈（常数优化 - 一次循环拿到左右最近下标）
     */
    @Test
    public void largestRectangleStackOptimizeTest() {
        System.out.println(largestRectangleStackOptimize(new int[]{2, 1, 5, 6, 2, 3}));
    }

    public int largestRectangleStackOptimize(int[] heights) {
        int n = heights.length;
        int[] left = new int[n];
        int[] right = new int[n];
        Arrays.fill(right, n);

        Stack<Integer> stack = new Stack<Integer>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
                // 出栈时计算右边最近
                right[stack.peek()] = i;
                stack.pop();
            }
            // 入栈时计算左边最近
            left[i] = (stack.isEmpty() ? -1 : stack.peek());
            stack.push(i);
        }

        int ans = 0;
        for (int i = 0; i < n; i++) {
            ans = Math.max(ans, (right[i] - left[i] - 1) * heights[i]);
        }
        return ans;
    }

}
