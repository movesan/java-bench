package datastructure;

/**
 * @description: 循环链表
 * @author: movesan
 * @create: 2020-08-21 17:12
 **/
public class LoopLinkedList {
    public int size;
    public Node head;

    /**
     * 添加元素
     *
     * @param obj
     * @return
     */
    public Node add(Object obj) {
        Node newNode = new Node(obj);
        if (size == 0) {
            head = newNode;
            head.next = head;
        } else {
            Node target = head;
            while (target.next != head) {
                target = target.next;
            }
            target.next = newNode;
            newNode.next = head;
        }
        size++;
        return newNode;
    }

    /**
     * 在指定位置插入元素
     *
     * @return
     */
    public Node insert(int index, Object obj) {
        if (index >= size) {
            return null;
        }
        Node newNode = new Node(obj);
        if (index == 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node target = head;
            Node previous = head;
            int pos = 0;
            while (pos != index) {
                previous = target;
                target = target.next;
                pos++;
            }
            previous.next = newNode;
            newNode.next = target;
        }
        size++;
        return newNode;
    }

    /**
     * 删除链表头部元素
     *
     * @return
     */
    public Node removeHead() {
        if (size > 0) {
            Node node = head;
            Node target = head;
            while (target.next != head) {
                target = target.next;
            }
            head = head.next;
            target.next = head;
            size--;
            return node;
        } else {
            return null;
        }
    }

    /**
     * 删除指定位置元素
     *
     * @return
     */
    public Node remove(int index) {
        if (index >= size) {
            return null;
        }
        Node result = head;
        if (index == 0) {
            head = head.next;
        } else {
            Node target = head;
            Node previous = head;
            int pos = 0;
            while (pos != index) {
                previous = target;
                target = target.next;
                pos++;
            }
            previous.next = target.next;
            result = target;
        }
        size--;
        return result;
    }

    /**
     * 删除指定元素
     *
     * @return
     */
    public Node removeNode(Object obj) {
        Node target = head;
        Node previoust = head;
        if (obj.equals(target.data)) {
            head = head.next;
            size--;
        } else {
            while (target.next != null) {
                if (obj.equals(target.next.data)) {
                    previoust = target;
                    target = target.next;
                    size--;
                    break;
                } else {
                    target = target.next;
                    previoust = previoust.next;
                }
            }
            previoust.next = target.next;
        }
        return target;
    }

    /**
     * 返回指定元素
     *
     * @return
     */
    public Node findNode(Object obj) {
        Node target = head;
        while (target.next != null) {
            if (obj.equals(target.data)) {
                return target;
            } else {
                target = target.next;
            }
        }
        return null;
    }

    /**
     * 输出链表元素
     */
    public void show() {
        if (size > 0) {
            Node node = head;
            int length = size;
            System.out.print("[");
            while (length > 0) {
                if (length == 1) {
                    System.out.print(node.data);
                } else {
                    System.out.print(node.data + ",");
                }
                node = node.next;
                length--;
            }
            System.out.println("]");
        } else {
            System.out.println("[]");
        }
    }
}
