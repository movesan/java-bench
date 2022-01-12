package leetcode.medium;

import org.junit.Test;

import java.util.Arrays;

/**
 * @description: 下一个排列
 * @author: movesan
 * @create: 2020-10-24 10:07
 **/
public class NextPermutation {

    /**
     * 遍历 : O(n)
     */
    @Test
    public void nextPermutationTest() {
        int[] nums = new int[]{9, 5, 3, 4, 1};
        nextPermutation(nums);
        System.out.println(Arrays.toString(nums));
    }

    public void nextPermutation(int[] nums) {
        int i = nums.length - 2;
        // 找到需要交换的位置
        while (i >= 0 && nums[i + 1] <= nums[i]) {
            i--;
        }

        // 将需要交换的i 与它右侧第一个比它大的数进行交换
        if (i >= 0) {
            int j = nums.length - 1;
            while (j >= 0 && nums[j] <= nums[i]) {
                j--;
            }
            swap(nums, i, j);
        }

        // 右侧已经是降序排序，需要反转成升序排序
        reverse(nums, i + 1);
    }

    /**
     * 反转数组
     * @param nums
     * @param start
     */
    private void reverse(int[] nums, int start) {
        int i = start, j = nums.length - 1;
        while (i < j) {
            swap(nums, i, j);
            i++;
            j--;
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
