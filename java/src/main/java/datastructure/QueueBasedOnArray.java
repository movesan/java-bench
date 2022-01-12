package datastructure;

/**
 * @description: 基于数组实现队列
 * @author: movesan
 * @create: 2020-04-26 10:51
 **/
public class QueueBasedOnArray {

    // 数组：items，数组大小：n
    private int[] items;
    private int n = 0;
    // head表示队头下标，tail表示队尾下标
    private int head = 0;
    private int tail = 0;

    // 申请一个大小为capacity的数组
    public QueueBasedOnArray(int capacity) {
        items = new int[capacity];
        n = capacity;
    }

    // 入队操作，将item放入队尾
    public boolean enqueue(int item) {
        // tail == n表示队列末尾没有空间了
        if (tail == n) {
            // tail ==n && head==0，表示整个队列都占满了
            if (head == 0) return false;
            // 数据搬移
            for (int i = head; i < tail; ++i) {
                items[i-head] = items[i];
            }
            // 搬移完之后重新更新head和tail
            tail -= head;
            head = 0;
        }

        items[tail] = item;
        ++tail;
        return true;
    }

    // 出队
    public int dequeue() {
        // 如果head == tail 表示队列为空
        if (head == tail) return -1;
        // 为了让其他语言的同学看的更加明确，把--操作放到单独一行来写了
        int res = items[head];
        ++head;
        return res;
    }

    public void printAll() {
        for (int i = head; i < tail; ++i) {
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }
}
