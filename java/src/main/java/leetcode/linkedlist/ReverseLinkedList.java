package leetcode.linkedlist;

/**
 * @description: 反转链表
 * @author: movesan
 * @create: 2020-09-15 15:28
 **/
public class ReverseLinkedList {

    /**  ================= 反转单链表 II 206 ================= **/

    /**
     * 迭代解法
     *
     * @param head
     * @return
     */
    public static ListNode reverse(ListNode head) {
        ListNode cur = head;
        ListNode prev = null;

        while (cur != null) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    /**
     * 递归解法
     *
     * @param head
     * @return
     */
    ListNode reverseRecursion(ListNode head) {
        if (head.next == null) return head;
        ListNode last = reverse(head.next);
        head.next.next = head;
        head.next = null;
        return last;
    }

    /**
     * ================= 反转指定区间链表 92:https://leetcode-cn.com/problems/reverse-linked-list-ii/=================
     **/

    ListNode successor = null; // 后驱节点

    /**
     * 反转以 head 为起点的 n 个节点，返回新的头结点
     */
    ListNode reverseN(ListNode head, int n) {
        if (n == 1) {
            // 记录第 n + 1 个节点
            successor = head.next;
            return head;
        }
        // 以 head.next 为起点，需要反转前 n - 1 个节点
        ListNode last = reverseN(head.next, n - 1);

        head.next.next = head;
        // 让反转之后的 head 节点和后面的节点连起来
        head.next = successor;
        return last;
    }

    /**
     * 反转指定区间链表
     *
     * @param head
     * @param m
     * @param n
     * @return
     */
    ListNode reverseBetween(ListNode head, int m, int n) {
        // base case
        if (m == 1) {
            return reverseN(head, n);
        }
        // 前进到反转的起点触发 base case
        head.next = reverseBetween(head.next, m - 1, n - 1);
        return head;
    }

    /**
     * ================= K 个一组翻转链表 25 =================
     **/
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode cur = head;
        ListNode prev = null;

        ListNode prevListNode = null;
        int i = 0;
        while (cur != null) {
            i++;
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
            if (i == k) {
                prevListNode = prev;
                prev = null;
            }
        }
        return prev;
    }

    public static void main(String[] args) {
        ListNode node = new ListNode(new int[]{1, 2, 3, 4, 5});
        System.out.println(reverse(node));
    }
}
