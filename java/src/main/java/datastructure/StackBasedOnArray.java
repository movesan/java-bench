package datastructure;

/**
 * @description: 基于数组实现栈
 * @author: movesan
 * @create: 2020-04-26 10:37
 **/
public class StackBasedOnArray {

    private int[] items;  // 数组
    private int count;       // 栈中元素个数
    private int n;           // 栈的大小

    // 初始化数组，申请一个大小为n的数组空间
    public StackBasedOnArray(int n) {
        this.items = new int[n];
        this.n = n;
        this.count = 0;
    }

    // 入栈操作
    public boolean push(int item) {
        // 数组空间不够了，直接返回false，入栈失败。
        if (count == n) return false;
        // 将item放到下标为count的位置，并且count加一
        items[count] = item;
        ++count;
        return true;
    }

    // 出栈操作
    public int pop() {
        // 栈为空，则直接返回null
        if (count == 0) return -1;
        // 返回下标为count-1的数组元素，并且栈中元素个数count减一
        int tmp = items[count-1];
        --count;
        return tmp;
    }


}
