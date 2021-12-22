package leetcode.linkedlist;

/**
 * @description: 两数相加 2:https://leetcode-cn.com/problems/add-two-numbers/
 * @author: movesan
 * @create: 2020-09-16 16:04
 **/
public class AdditionLinkedList {

    public static ListNode addTwoNumbers(ListNode left, ListNode right) {
        ListNode dummy = new ListNode(-1);
        ListNode cur = dummy;

        boolean carry = false;
        while ((left != null && right != null) || carry) {
            int leftVal = 0;
            int rightVal = 0;
            if (left != null) {
                leftVal = left.val;
                left = left.next;
            }
            if (right != null) {
                rightVal = right.val;
                right = right.next;
            }
            int sum = leftVal + rightVal;
            if (carry) {
                sum++;
                carry = false;
            }
            if (sum >= 10) {
                carry = true;
                sum -= 10;
            }
            cur.next = new ListNode(sum);
            cur = cur.next;
        }

        if (left != null) {
            cur.next = left;
        }
        if (right != null) {
            cur.next = right;
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        ListNode left = new ListNode(new int[]{5, 9});
        ListNode right = new ListNode(new int[]{5});
        System.out.println(addTwoNumbers(left, right));

    }
}
