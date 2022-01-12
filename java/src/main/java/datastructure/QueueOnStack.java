package datastructure;

import java.util.Stack;

/**
 * @description: 栈实现队列
 * @author: movesan
 * @create: 2020-10-20 15:06
 **/
public class QueueOnStack {

    private Stack<Integer> stackIn = new Stack<Integer>();
    private Stack<Integer> stackOut = new Stack<Integer>();

    /** Initialize your data structure here. */
    public QueueOnStack() {

    }

    /** Push element x to the back of queue. */
    public void push(int x) {
        stackIn.push(x);
    }

    /** Removes the element from in front of queue and returns that element. */
    public int pop() {
        if (stackOut.size() == 0) {
            while (stackIn.size() > 0) {
                stackOut.push(stackIn.pop());
            }
        }
        return stackOut.pop();
    }

    /** Get the front element. */
    public int peek() {
        if (stackOut.size() == 0) {
            while (stackIn.size() > 0) {
                stackOut.push(stackIn.pop());
            }
        }
        return stackOut.peek();
    }

    /** Returns whether the queue is empty. */
    public boolean empty() {
        return stackIn.size() == 0 && stackOut.size() == 0;
    }
}
