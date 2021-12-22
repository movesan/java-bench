package leetcode.binarytree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static leetcode.binarytree.TreeNode.getTree;

/**
 * @description: 前序遍历
 * @author: movesan
 * @create: 2020-09-17 17:30
 **/
public class PreOrderTraversal {

    /**
     * 迭代遍历
     * @param root
     * @return
     */
    public List<Integer> iterationTraversal(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        if (root == null) return list;

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            list.add(node.val);
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
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
        res.add(root.val);
        recursion(root.left, res);
        recursion(root.right, res);
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
