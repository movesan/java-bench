package leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 几数之和
 * @author: movesan
 * @create: 2020-10-12 22:48
 **/
public class SumCollection {

    /**
     * 两数之和（常规hashmap 解法）
     *
     * @param nums
     * @param target
     * @return
     */
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int other = target - nums[i];
            if (map.containsKey(other)) {
                return new int[]{map.get(other), i};
            }
            map.put(nums[i], i);
        }
        throw new IllegalArgumentException("No two sum solution");
    }

    /**
     * 两数之和（双指针解法）
     *
     * @param nums
     * @param target
     * @return
     */
    public int[] twoSum2(int[] nums, int target) {
        int[] res = new int[2];
        // 1.排序
        Arrays.sort(nums);
        int L = 0;
        int R = nums.length - 1;
        for (int i = 0; i < nums.length; i++) {
            if (L >= R) break;
            int sum = nums[L] + nums[R];
            if (sum == target) {
                res[0] = L;
                res[1] = R;
            } else if (sum < target) {
                L++;
            } else {
                R--;
            }
        }
        return res;
    }

    /**
     * 三数之和
     *
     * @param nums
     * @return
     */
    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> ans = new ArrayList();
        int len = nums.length;
        if (nums == null || len < 3) return ans;
        Arrays.sort(nums); // 排序
        for (int i = 0; i < len; i++) {
            if (nums[i] > 0) break; // 如果当前数字大于0，则三数之和一定大于0，所以结束循环
            if (i > 0 && nums[i] == nums[i - 1]) continue; // 去重
            int L = i + 1;
            int R = len - 1;
            while (L < R) {
                int sum = nums[i] + nums[L] + nums[R];
                if (sum == 0) {
                    ans.add(Arrays.asList(nums[i], nums[L], nums[R]));
                    while (L < R && nums[L] == nums[L + 1]) L++; // 去重
                    while (L < R && nums[R] == nums[R - 1]) R--; // 去重
                    L++;
                    R--;
                } else if (sum < 0) L++;
                else if (sum > 0) R--;
            }
        }
        return ans;
    }

    /**
     * 四数之和
     *
     * @param nums
     * @param target
     * @return
     */
    public List<List<Integer>> fourSum(int[] nums, int target) {
        // 参考三数之和
        return null;
    }
}
