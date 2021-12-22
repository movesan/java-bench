package leetcode.binarytree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static leetcode.binarytree.TreeNode.getTree;

/**
 * @description: 后序遍历
 * @author: movesan
 * @create: 2020-09-17 17:30
 **/
public class PostOrderTraversal {

    /**
     * 迭代遍历
     * @param root
     * @return
     */
    public List<Integer> iterationTraversal(TreeNode root) {
        LinkedList<Integer> list = new LinkedList<>();
        if (root == null) return list;

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            list.addFirst(node.val);
            if (node.left != null) {
                stack.push(node.left);
            }
            if (node.right != null) {
                stack.push(node.right);
            }
        }
        return list;
    }

    /**
     * 递归遍历
     * @param root
     * @return
     */
    public List<Integer> recursionTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        recursion(root, res);
        return res;
    }

    private void recursion(TreeNode root, List<Integer> res) {
        if (root == null) return;
        recursion(root.left, res);
        recursion(root.right, res);
        res.add(root.val);
    }

    @Test
    public void recursionTest() {
        TreeNode node = getTree();
        System.out.println(recursionTraversal(node));
    }

    @Test
    public void iterationTest() {
        TreeNode node = getTree();
        System.out.println(iterationTraversal(node));
    }
}
