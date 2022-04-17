package leetcode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

/**
 * @description: 单词问题
 * @author: movesan
 * @create: 2020-10-24 22:34
 **/
public class WordIssue {

    /**
     * 反转单词（双指针）  58:https://leetcode-cn.com/problems/fan-zhuan-dan-ci-shun-xu-lcof/
     *
     * @param s
     * @return
     */
    public String reverseWords(String s) {
        s = s.trim(); // 删除首尾空格
        StringBuilder res = new StringBuilder();

        int j = s.length() - 1;
        int i = j;
        while (i >= 0) {
            while (i >= 0 && s.charAt(i) != ' ') {
                i--; // 搜索首个空格
            }
            res.append(s.substring(i + 1, j + 1)).append(" "); // 添加单词
            while (i >= 0 && s.charAt(i) == ' ') {
                i--; // 跳过单词间空格
            }
            j = i; // j 指向下个单词的尾字符
        }
        return res.toString().trim(); // 转化为字符串并返回
    }

    /**
     * 有效括号
     * @param s
     * @return
     */
    public boolean isValid(String s) {
        int n = s.length();
        if (n % 2 == 1) { return false; }

        Stack<Character> stack = new Stack<>();
        Map<Character, Character> map = new HashMap<>();
        map.put(')', '(');
        map.put('}', '{');
        map.put(']', '[');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!stack.isEmpty() && stack.peek() != null && stack.peek() == map.get(c)) {
                stack.pop();
                continue;
            }
            stack.add(c);
        }
        return stack.isEmpty();
    }
}
