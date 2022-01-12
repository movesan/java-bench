package datastructure;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @description: 队列实现栈
 * @author: movesan
 * @create: 2020-10-20 15:06
 **/
public class StackOnQueue {

    private Queue<Integer> queue = new LinkedList<Integer>();
    private Queue<Integer> queueTemp = new LinkedList<Integer>();
    private Integer top;

    /**
     * Initialize your data structure here.
     */
    public StackOnQueue() {

    }

    /**
     * Push element x onto stack.
     */
    public void push(int x) {
        queue.add(x);
        top = x;
    }

    /**
     * Removes the element on top of the stack and returns that element.
     */
    public int pop() {
        while (queue.size() > 1) {
            int newTop = queue.remove();
            queueTemp.add(newTop);
            top = newTop;
        }
        int pop = queue.remove();
        Queue<Integer> temp = queueTemp;
        queueTemp = queue;
        queue = temp;
        return pop;
    }

    /**
     * Get the top element.
     */
    public int top() {
        return top;
    }

    /**
     * Returns whether the stack is empty.
     */
    public boolean empty() {
        return queue.size() == 0;
    }
}
