import leetcode.linkedlist.ListNode;
import org.junit.Test;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description: 手稿
 * @author: movesan
 * @create: 2020-10-12 11:16
 **/
public class ZManuscripts {

    private boolean flag = false;

    public static void main(String[] args) {

    }

    @Test
    public void test() {
        Lock lock = new ReentrantLock();
        Condition conditionA = lock.newCondition();
        Condition conditionB = lock.newCondition();
        new Thread(() -> {
            int i = 1;
            while (i <= 100) {
                lock.lock();
                try {
                    while (flag) {
                        conditionA.await();
                    }
                    flag = true;
                    System.out.println(Thread.currentThread().getName() + i);
                    i = i + 2;
                    conditionB.signal();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }, "打印奇数：").start();

        new Thread(() -> {
            int i = 2;
            while (i <= 100) {
                lock.lock();
                try {
                    while (!flag) {
                        conditionB.await();
                    }
                    flag = false;
                    System.out.println(Thread.currentThread().getName() + i);
                    i = i + 2;
                    conditionA.signal();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }, "打印偶数：").start();

    }

    public static ListNode merge(ListNode left, ListNode right) {
        ListNode dummy = new ListNode(-1);
        ListNode cur = dummy;

        while (left != null && right != null) {
            if (left.val < right.val) {
                cur.next = left;
                cur = cur.next;
                left = left.next;
            } else {
                cur.next = right;
                cur = cur.next;
                right = right.next;
            }
        }
        cur.next = left != null ? left : right;
        return dummy.next;
    }

    public ListNode mergeKListsPriority(ListNode[] lists) {
        PriorityQueue<ListNode> queue = new PriorityQueue<>(new Comparator<ListNode>() {
            @Override
            public int compare(ListNode o1, ListNode o2) {
                return Integer.compare(o1.val, o2.val);
            }
        });
        for (ListNode node : lists) {
            if (node != null) {
                queue.add(node);
            }
        }

        ListNode dummy = new ListNode(-1);
        ListNode sorted = dummy;
        while (!queue.isEmpty()) {
            sorted.next = queue.poll();
            sorted = sorted.next;
            if (sorted != null) {
                queue.add(sorted);
            }
        }
        return dummy.next;
    }
}
