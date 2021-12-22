package leetcode.linkedlist;

import org.junit.Test;

/**
 * @description: 环形链表
 * @author: movesan
 * @create: 2020-09-16 22:47
 **/
public class CycleLinkedList {

    /**
     * 探测入环节点
     * @param head
     * @return
     */
    public ListNode detectCycleNode(ListNode head) {
        ListNode fast = head;
        ListNode slow = head;
        ListNode meetNode = null;
        // 探测环，得到相遇节点
        while (slow != null && fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                meetNode = fast;
                break;
            }
        }
        if (meetNode == null) {
            return null;
        } else {
            // 头节点与相遇节点再遇到时走过的路程相同
            ListNode part1 = meetNode;
            ListNode part2 = head;
            while (part1 != part2) {
                part1 = part1.next;
                part2 = part2.next;
            }
            return part1;
        }
    }

    @Test
    public void test() {
        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(3);
        ListNode node4 = new ListNode(4);
        ListNode node5 = new ListNode(5);
        ListNode node6 = new ListNode(6);

        node1.next(node2).next(node3).next(node4).next(node5).next(node6)
                .next(node3); // node3为入环节点
        System.out.println(detectCycleNode(node1).val);
    }
}
