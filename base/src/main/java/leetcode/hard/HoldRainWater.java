package leetcode.hard;

import java.util.Stack;
import org.junit.Test;

/**
 * @description: 接雨水
 * @author: movesan
 * @create: 2020-10-25 09:54
 **/
public class HoldRainWater {

    /**
     * 栈解法
     *
     * @param height
     * @return
     */
    public int trap(int[] height) {
        int sum = 0;

        // 存储下标
        Stack<Integer> stack = new Stack<>();
        int current = 0;
        while (current < height.length) {
            //如果栈不空并且当前指向的高度大于栈顶高度就一直循环
            while (!stack.empty() && height[current] > height[stack.peek()]) {
                int h = height[stack.peek()]; //取出要出栈的元素
                stack.pop(); //出栈
                if (stack.empty()) { // 栈空就出去
                    break;
                }
                int distance = current - stack.peek() - 1; //两堵墙之前的距离。
                // 选出栈顶元素与当前元素高度的最小值
                int min = Math.min(height[stack.peek()], height[current]);
                // 最小值对弹出的元素做差，就是高度（这里高度可能为负数，不过会在下一次计算时补回来，不影响最终结果，原理是按层进行计算）
                sum = sum + distance * (min - h);
            }
            stack.push(current); //当前指向的墙入栈
            current++; //指针后移
        }
        return sum;
    }

    @Test
    public void test() {
        int[] height = new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        System.out.println(trap(height));
    }
}
