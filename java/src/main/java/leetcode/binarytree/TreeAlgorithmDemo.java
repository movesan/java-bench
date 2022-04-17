package leetcode.binarytree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * @description: 二叉树的一些算法 demo
 * @author: movesan
 * @create: 2020-09-18 10:13
 **/
public class TreeAlgorithmDemo {

    /**
     * 二叉树右视图
     *
     * @param root
     * @return
     */
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) return res;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
                if (i == size - 1) {
                    res.add(node.val);
                }
            }
        }
        return res;
    }

    /**
     * 二叉树的最近公共祖先（迭代）
     * 此遍历方式应该是按单位树从左到右遍历，不是真正的按层遍历
     *
     * @param root
     * @param p
     * @param q
     * @return
     */
    public TreeNode lowestCommonAncestorIteration(TreeNode root, TreeNode p, TreeNode q) {
        // 每个节点直接父节点
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        Queue<TreeNode> queue = new LinkedList<>();
        parent.put(root, null);
        queue.add(root);
        while ((!parent.containsKey(p) || !parent.containsKey(q))) {
            TreeNode node = queue.poll();
            if (node.left != null) {
                parent.put(node.left, node);
                queue.add(node.left);
            }
            if (node.right != null) {
                parent.put(node.right, node);
                queue.add(node.right);
            }
        }

        // p 节点的所有祖先
        Set<TreeNode> ancestors = new HashSet<>();
        while (p != null) {
            ancestors.add(p);
            p = parent.get(p);
        }
        // 查看p 和他的祖先节点是否包含 q节点，如果不包含是否包含其父节点
        while (!ancestors.contains(q)) {
            q = parent.get(q);
        }
        return q;
    }

    /**
     * 二叉树的最近公共祖先（递归）
     *
     * @param root
     * @param p
     * @param q
     * @return
     */
    public TreeNode lowestCommonAncestorRecursion(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left = lowestCommonAncestorRecursion(root.left, p, q);
        TreeNode right = lowestCommonAncestorRecursion(root.right, p, q);
        //如果left为空，说明这两个节点在root结点的右子树上，我们只需要返回右子树查找的结果即可
        if (left == null) return right;
        if (right == null) return left;
        //如果left和right都不为空，说明这两个节点一个在root的左子树上一个在root的右子树上，
        //我们只需要返回root结点即可。
        return root;
    }

    /**
     * 翻转二叉树
     * @param root
     * @return
     */
    public TreeNode reverse(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            TreeNode left = cur.left;
            TreeNode right = cur.right;
            cur.left = right;
            cur.right = left;
            cur = right;
        }
        return root;

    }

    @Test
    public void reverseTest() {
        reverse(TreeNode.getTree()).show();
    }

    @Test
    public void rightSideViewTest() {
        System.out.println("右视图：" + rightSideView(TreeNode.getTree()));
    }

    @Test
    public void lowestCommonAncestorTest() {
        /*
                     5
                   /   \
                  3     7
                /  \   /  \
               2    4 6    9
              /           / \
             1           8   10
         */
        TreeNode p = new TreeNode(6);
        TreeNode q = new TreeNode(10);

        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(3);
        root.right = new TreeNode(7);

        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.left.left.left = new TreeNode(1);
//        root.right.right.right

        root.right.left = p;
        root.right.right = new TreeNode(9);
        root.right.right.left = new TreeNode(8);
        root.right.right.right = q;

//        System.out.println(lowestCommonAncestorIteration(root, p, q).val);
//        System.out.println(lowestCommonAncestorRecursion(root, p, q));
    }

}
