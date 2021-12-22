package leetcode.linkedlist;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * @description: 排序链表 148:https://leetcode-cn.com/problems/sort-list/
 * @author: movesan
 * @create: 2020-09-16 18:02
 **/
public class SortLinkedList {

    /**
     * 归并（迭代）
     *
     * @param head
     * @return
     */
    public ListNode sortListMergeIteration(ListNode head) {
        // 归并排序
        int n = 0;
        // 走到null，刚好走链表的长度
        for (ListNode i = head; i != null; i = i.next) n++;

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        // 循环一下
        // 第一层循环，分块，从1个一块，2个一块，4个一块，直到n个一块，
        for (int i = 1; i < n; i = 2 * i) {
            ListNode begin = dummy;
            // 开始归并
            // j + i >= n 表示只有一段就不归并了，因为已经是排好序的
            for (int j = 0; j + i < n; j = j + 2 * i) {
                // 两块，找两块的起始节点
                // 开始都指向第一块的起点
                // 然后second走n步指向第二块的起点
                ListNode first = begin.next, second = first;
                for (int k = 0; k < i; k++) second = second.next;

                // 遍历第一块和第二块进行归并
                // 第一块的数量为i
                // 第二块的数量为i也可能小于i，所以循环条件要加一个second != null
                int f = 0, s = 0;
                while (f < i && s < i && second != null) {
                    if (first.val < second.val) {
                        begin.next = first;
                        begin = begin.next;
                        first = first.next;
                        f++;
                    } else {
                        begin.next = second;
                        begin = begin.next;
                        second = second.next;
                        s++;
                    }
                }
                // 归并之后可能又多余的没有处理
                while (f < i) {
                    begin.next = first;
                    begin = begin.next;
                    first = first.next;
                    f++;

                }
                while (s < i && second != null) {
                    begin.next = second;
                    begin = begin.next;
                    // second已经更新到下一块的起点了
                    second = second.next;
                    s++;
                }

                // 更新begin
                // begin.next 指向下一块的起点
                begin.next = second;
            }
        }
        return dummy.next;
    }

    /**
     * 归并（递归）
     *
     * @param head
     * @return
     */
    public ListNode sortListMergeRecursion(ListNode head) {
        if (head == null || head.next == null)
            return head;
        // 1.快慢指针找到中间节点
        ListNode fast = head.next, slow = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode tmp = slow.next;
        slow.next = null;

        // 2.分解
        ListNode left = sortListMergeRecursion(head);
        ListNode right = sortListMergeRecursion(tmp);

        // 3.合并:合并两个有序链表
        ListNode dummy = new ListNode(-1);
        ListNode cur = dummy;
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
     * 快排
     *
     * @param head
     * @return
     */
    public ListNode sortListQuick(ListNode head) {
        return quickSort(head, null);
    }

    // sort[lo..hi), hi is exclusive
    private ListNode quickSort(ListNode lo, ListNode hi) {
        if (lo == hi)
            return lo;
        ListNode k = partition(lo, hi);
        quickSort(lo, k);
        quickSort(k.next, hi);
        return lo;
    }

    // using low bound as pivot
    // partite [lo..hi)
    private ListNode partition(ListNode lo, ListNode hi) {
        ListNode k = lo;    // partition point
        int pivot = lo.val;
        for (ListNode i = lo.next; i != hi && i != null; i = i.next) {
            if (i.val < pivot) {
                swap(k.next, i);
                k = k.next;
            }
        }
        swap(lo, k);
        return k;
    }

    private void swap(ListNode a, ListNode b) {
        int t = a.val;
        a.val = b.val;
        b.val = t;
    }

    /**
     * 此方式采用二分插入方法 TODO 待验证
     *
     * @param head
     * @return
     */
    public ListNode sortList(ListNode head) {
        ListNode dummy = new ListNode(-1);
        List<ListNode> sortedList = new ArrayList<>();
        while (head != null) {
            ListNode newNode = new ListNode(head.val);
            ListNode pre = binarySearch(sortedList, head.val);
            if (pre == null) {
                dummy.next = newNode;
            } else {
                ListNode next = pre.next;
                pre.next = newNode;
                newNode.next = next;
            }
            head = head.next;
        }

        return dummy.next;
    }

    /**
     * 二分插入，此方法需要 sortedList 保持有序
     *
     * @param sortedList
     * @param val
     * @return
     */
    private ListNode binarySearch(List<ListNode> sortedList, int val) {
        int size = sortedList.size();
        int lo = 0;
        int hi = size - 1;
        while (lo <= hi) {
            int mid = lo + ((hi - lo) >> 1);
            if (sortedList.get(mid).val > val) {
                hi = mid - 1;
            } else {
                if ((mid == size - 1) || (sortedList.get(mid + 1).val > val)) {
                    return sortedList.get(mid);
                } else {
                    lo = mid + 1;
                }
            }
        }
        return null;
    }

    @Test
    public void test() {
        System.out.println(sortListQuick(new ListNode(new int[]{3, 2, 5, 1, 6, 8, 7})));
    }

    @Test
    public void quickTest() {
        System.out.println(sortListQuick(new ListNode(new int[]{3, 2, 5, 1, 6, 8, 7})));
    }

    @Test
    public void recursionMergeTest() {
        System.out.println(sortListMergeRecursion(new ListNode(new int[]{3, 2, 5, 1, 6, 8, 7})));
    }
}
