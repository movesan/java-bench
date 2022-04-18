package leetcode.linkedlist;

import org.junit.Test;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @description: 合并两个有序链表 leetcode: 剑指offer 25
 * @author: movesan
 * @create: 2020-09-14 16:41
 **/
public class MergeLinkedList {

    /**
     * 循环的方式合并
     *
     * @param left
     * @param right
     * @return
     */
    public ListNode mergeTwoLists(ListNode left, ListNode right) {
        ListNode dummy = new ListNode(-1);
        ListNode cur = dummy;

        // 比较左右链表
        while (left != null && right != null) {
            if (left.val <= right.val) {
                cur.next = left;
                cur = left;
                left = left.next;
            } else {
                cur.next = right;
                cur = right;
                right = right.next;
            }
        }
        cur.next = left != null ? left : right;

        return dummy.next;
    }

    /**
     * 合并K 个升序链表 23:https://leetcode-cn.com/problems/merge-k-sorted-lists/
     *
     * @param lists
     * @return
     */
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        ListNode temp = lists[0];
        for (int i = 1; i < lists.length; i++) {
            temp = mergeTwoLists(temp, lists[i]);
        }
        return temp;
    }

    /**
     * 合并K 个升序链表 （优先队列）
     *
     * @param lists
     * @return
     */
    public ListNode mergeKListsPriority(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
//        PriorityQueue<ListNode> queue = new PriorityQueue<>(lists.length, Comparator.comparingInt(o -> o.val));
        PriorityQueue<ListNode> queue = new PriorityQueue<>(lists.length, (e1, e2) -> e1.val - e2.val);
//        PriorityQueue<ListNode> queue = new PriorityQueue<>(lists.length, new Comparator<ListNode>() {
//            @Override
//            public int compare(ListNode o1, ListNode o2) {
//                if (o1.val < o2.val) return -1;
//                else if (o1.val == o2.val) return 0;
//                else return 1;
//            }
//        });
        ListNode dummy = new ListNode(-1);
        ListNode sortedNode = dummy;
        for (ListNode node : lists) {
            if (node != null) queue.add(node);
        }
        while (!queue.isEmpty()) {
            sortedNode.next = queue.poll();
            sortedNode = sortedNode.next;
            if (sortedNode.next != null) queue.add(sortedNode.next);
        }
        return dummy.next;
    }

    @Test
    public void mergeTwoListsTest() {
        ListNode node1 = new ListNode(new int[]{1, 3, 5, 7});
        ListNode node2 = new ListNode(new int[]{2, 4, 6, 8});
        ListNode res = mergeTwoLists(node1, node2);
        System.out.println(res);
    }

    @Test
    public void mergeKListsTest() {
        ListNode[] lists = new ListNode[2];
        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node6 = new ListNode(6);
        ListNode node7 = new ListNode(7);
        node1.next(node2).next(node6).next(node7);
        lists[0] = node1;

        ListNode node3 = new ListNode(3);
        ListNode node4 = new ListNode(4);
        ListNode node5 = new ListNode(5);
        ListNode node8 = new ListNode(8);
        node3.next(node4).next(node5).next(node8);
        lists[1] = node3;

        System.out.println(mergeKListsPriority(lists));
    }
}
