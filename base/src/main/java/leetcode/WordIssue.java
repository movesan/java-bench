package leetcode;

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
}
