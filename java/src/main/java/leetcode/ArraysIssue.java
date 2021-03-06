package leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @description: 数组类
 * @author: movesan
 * @create: 2020-10-24 20:59
 **/
public class ArraysIssue {

    /**  ================= 两个数组的交集 II 350:https://leetcode-cn.com/problems/intersection-of-two-arrays-ii/ ================= **/

    /**
     * 两个数组的交集 II
     *
     * @param nums1
     * @param nums2
     * @return
     */
    public int[] intersect(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            return intersect(nums2, nums1);
        }
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        // 遍历第一个数组，记录出现的次数
        for (int num : nums1) {
            int count = map.getOrDefault(num, 0) + 1;
            map.put(num, count);
        }
        int[] intersection = new int[nums1.length];
        int index = 0;
        // 遍历第二个数组，如果第一个数组中存在，则放入结果集中，并且第一个数组中的对应次数减一
        for (int num : nums2) {
            int count = map.getOrDefault(num, 0);
            if (count > 0) {
                intersection[index++] = num;
                count--;
                if (count > 0) {
                    map.put(num, count);
                } else {
                    map.remove(num);
                }
            }
        }
        return Arrays.copyOfRange(intersection, 0, index);
    }


    /**  ================= 连续子数组的最大和  II 42:https://leetcode-cn.com/problems/lian-xu-zi-shu-zu-de-zui-da-he-lcof/ ================= **/
    /**
     * 连续子数组的最大和
     *
     * @param nums
     * @return
     */
    public int maxSubArray(int[] nums) {
        int res = nums[0];
        for (int i = 1; i < nums.length; i++) {
            nums[i] += Math.max(nums[i - 1], 0);
            res = Math.max(res, nums[i]);
        }
        return res;
    }

    public int maxSubArrayDP(int[] nums) {
        if (nums == null || nums.length <= 0) {
            return 0;
        }

        int length = nums.length;
        int[] dp = new int[length]; // 动归数组
        dp[0] = nums[0];
        int maxSum = nums[0];
        for (int i = 1; i < length; i++) {
            if (dp[i - 1] > 0) {
                dp[i] = dp[i - 1] + nums[i];
            } else {
                dp[i] = nums[i];
            }
            maxSum = Math.max(maxSum, dp[i]);
        }

        return maxSum;
    }

    /**  ================= 合并 n 个 区间 56:https://leetcode-cn.com/problems/merge-intervals/ ================= **/
    /**
     * 合并 n 个 区间
     *
     * @param intervals
     * @return
     */
    public int[][] merge(int[][] intervals) {
        // 先按照区间起始位置排序
        Arrays.sort(intervals, Comparator.comparingInt(v -> v[0]));
        // 遍历区间
        int[][] res = new int[intervals.length][2];
        int idx = -1;
        for (int[] interval : intervals) {
            // 如果结果数组是空的，或者当前区间的起始位置 > 结果数组中最后区间的终止位置，
            // 则不合并，直接将当前区间加入结果数组。
            if (idx == -1 || interval[0] > res[idx][1]) {
                res[++idx] = interval;
            } else {
                // 反之将当前区间合并至结果数组的最后区间
                res[idx][1] = Math.max(res[idx][1], interval[1]);
            }
        }
        return Arrays.copyOf(res, idx + 1);
    }


    /**  ================= 全排列  46:https://leetcode-cn.com/problems/permutations/ ================= **/
    /**
     * 全排列（回溯）
     *
     * @param nums
     * @return
     */
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();

        List<Integer> output = new ArrayList<Integer>();
        for (int num : nums) {
            output.add(num);
        }

        int n = nums.length;
        backtrack(n, output, res, 0);
        return res;
    }

    public void backtrack(int n, List<Integer> output, List<List<Integer>> res, int first) {
        // 所有数都填完了
        if (first == n) {
            res.add(new ArrayList<Integer>(output));
        }
        for (int i = first; i < n; i++) {
            // 动态维护数组
            Collections.swap(output, first, i);
            // 继续递归填下一个数
            backtrack(n, output, res, first + 1);
            // 撤销操作
            Collections.swap(output, first, i);
        }
    }


    /**
     * 解法二
     *
     * @param nums
     * @return
     */
    public List<List<Integer>> permute2(int[] nums) {
        int len = nums.length;
        // 使用一个动态数组保存所有可能的全排列
        List<List<Integer>> res = new ArrayList<>();
        if (len == 0) {
            return res;
        }

        boolean[] used = new boolean[len];
        List<Integer> path = new ArrayList<>();

        dfs(nums, len, 0, path, used, res);
        return res;
    }

    private void dfs(int[] nums, int len, int depth,
                     List<Integer> path, boolean[] used,
                     List<List<Integer>> res) {
        // 当深度达到时即返回
        if (depth == len) {
            res.add(path);
            return;
        }

        // 在非叶子结点处，产生不同的分支，这一操作的语义是：在还未选择的数中依次选择一个元素作为下一个位置的元素，这显然得通过一个循环实现。
        for (int i = 0; i < len; i++) {
            if (!used[i]) {
                path.add(nums[i]);
                used[i] = true;

                dfs(nums, len, depth + 1, path, used, res);
                // 注意：下面这两行代码发生 「回溯」，回溯发生在从 深层结点 回到 浅层结点 的过程，代码在形式上和递归之前是对称的
                used[i] = false;
                path.remove(path.size() - 1);
            }
        }
    }

    /**  ================= 合并两个有序数组  88:https://leetcode-cn.com/problems/merge-sorted-array/ ================= **/
    /**
     * 合并两个有序数组
     *
     * @param nums1
     * @param m
     * @param nums2
     * @param n
     */
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int p1 = m - 1;
        int p2 = n - 1;
        int p = m + n - 1;
        while ((p1 >= 0) && (p2 >= 0))
            nums1[p--] = (nums1[p1] < nums2[p2]) ? nums2[p2--] : nums1[p1--];

        System.arraycopy(nums2, 0, nums1, 0, p2 + 1);
    }

    public void merge2(int[] nums1, int m, int[] nums2, int n) {
        int p1 = m - 1;
        int p2 = n - 1;
        int p = m + n - 1;
        while (p >= 0) {
            if (p1 < 0) {
                nums1[p--] = nums2[p2--];
                continue;
            }
            if (p2 < 0) {
                break;
            }
            if (nums1[p1] <= nums2[p2]) {
                nums1[p--] = nums2[p2--];
            } else {
                nums1[p--] = nums1[p1--];
            }
        }
    }

    /**  ================= 数组中第K个最大元素  215 ================= **/
    public int findKthLargest(int[] nums, int k) {
        return sort(nums, 0, nums.length - 1, nums.length - k);
    }

    private int sort(int[] nums, int l, int r, int index) {
        int p = partition(nums, l, r);
        if (p == index) {
            return nums[p];
        } else {
            return p < index ? sort(nums, p + 1, r, index) : sort(nums, l, p - 1, index);
        }
    }

    private int partition(int[] nums, int l, int r) {
        int v = nums[r];
        int i = l;
        for (int j = l; j < r; j++) {
            if (nums[j] < v) {
                swap(nums, i, j);
                i++;
            }
        }
        swap(nums, i, r);
        return i;
    }

    private void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    /**  ================= 字符串相加 415 ================= **/
    public static String addStrings(String num1, String num2) {
        StringBuilder res = new StringBuilder("");
        int i = num1.length() - 1;
        int j = num2.length() - 1;
        int carry = 0;
        while (i >= 0 || j >= 0) {
            //  - '0' ：char 转 int
            int n1 = i >= 0 ? num1.charAt(i) - '0' : 0;
            int n2 = j >= 0 ? num2.charAt(j) - '0' : 0;
            int tmp = n1 + n2 + carry;
            carry = tmp / 10;
            res.append(tmp % 10);
            i--;
            j--;
        }
        if (carry == 1) res.append(1);
        return res.reverse().toString();
    }

    /**  ================= 螺旋矩阵 54 ================= **/
    public static List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> list = new ArrayList<Integer>();
        if (matrix == null || matrix.length == 0) return list;
        int height = matrix.length;
        int width = matrix[0].length;
        int i = 0;
        // 统计矩阵从外向内的层数，如果矩阵非空，那么它的层数至少为1层
        int count = (Math.min(height, width) + 1) / 2;
        // 从外部向内部遍历，逐层打印数据
        while (i < count) {
            for (int j = i; j < width - i; j++) {
                list.add(matrix[i][j]);
            }
            for (int j = i + 1; j < height - i; j++) {
                list.add(matrix[j][(width - 1) - i]);
            }
            for (int j = (width - 1) - (i + 1); j >= i && (height - 1 - i != i); j--) {
                list.add(matrix[(height - 1) - i][j]);
            }
            for (int j = (height - 1) - (i + 1); j >= i + 1 && (width - 1 - i) != i; j--) {
                list.add(matrix[j][i]);
            }
            i++;
        }
        return list;
    }

    @Test
    public void test() {
        int[] nums = new int[]{2, 1, 3, 4, 1, 2, 1, 5, 4};
//        int res = maxSubArray(nums);
//        int res = maxSubArrayDP(nums);


//        System.out.println(lengthOfLIS(nums));
    }
}
