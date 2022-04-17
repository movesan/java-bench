package leetcode.slidingwindow;

import org.junit.Test;

import java.util.HashMap;

/**
 * @description: 最长不重复子串
 * @author: movesan
 * @create: 2020-10-24 11:21
 **/
public class LongestSubstring {

    /**
     * 最长不重复子串长度（滑动窗口） leetcode: 3
     *
     * @return
     */
    @Test
    public void lengthOfLongestSubstringTest() {
        System.out.println(lengthOfLongestSubstring("abcadccbb"));
    }

    public int lengthOfLongestSubstring(String s) {
        if (s.length() == 0) return 0;
        // 每个字符串以及所对应的位置，位置信息用于调整窗口的大小
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        int max = 0;
        int left = 0; // 窗口最左边的位置

        for (int i = 0; i < s.length(); i++) {
            // 如果出现重复字符串，需要调整窗口的开始位置
            if (map.containsKey(s.charAt(i))) {
                left = Math.max(left, map.get(s.charAt(i)) + 1);
            }
            map.put(s.charAt(i), i);
            max = Math.max(max, i - left + 1);
        }
        return max;

    }
}
