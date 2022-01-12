package leetcode.medium;

import java.util.Stack;

/**
 * @description: 132 模式   456：https://leetcode-cn.com/problems/132-pattern/
 * @author: movesan
 * @create: 2020-10-24 22:13
 **/
public class OneThreeTwoPattern {

    /**
     * 单调栈找出次大值
     * @param nums
     * @return
     */
    public boolean find132pattern(int[] nums) {
        if (nums.length < 3)
            return false;

        int second = Integer.MIN_VALUE;
        Stack<Integer> stack = new Stack<>();
        stack.add(nums[nums.length - 1]);
        for (int i = nums.length - 2; i >= 0; i--) {
            if (nums[i] < second) {
                return true;
            } else {
                // 找出次大值
                while (!stack.isEmpty() && nums[i] > stack.peek()) {
                    second = Math.max(stack.pop(), second);
                }
                stack.push(nums[i]);
            }
        }
        return false;
    }
}
