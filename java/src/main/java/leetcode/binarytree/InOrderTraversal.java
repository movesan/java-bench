package leetcode.binarytree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static leetcode.binarytree.TreeNode.getTree;

/**
 * @description: 中序遍历
 * @author: movesan
 * @create: 2020-09-17 17:29
 **/
public class InOrderTraversal {

    /**
     * 迭代遍历
     *      主要思想：
     *          将左枝放入栈中，pop 返回的节点即是左节点，然后找右节点，
     *              1.如果当前为叶子节点，那么右节点为null，此时 stack 不为空，下一个pop 返回的将是小分支上的 root 节点
     *              2.如果当前节点为小分支 root 节点，pop 将返回右节点
 *              所以整体顺序为  left -> root -> right
     *          不断重复两个步骤
     * @param root
     * @return
     */
    public List<Integer> iterationTraversal(TreeNode root) {
        ArrayList<Integer> list = new ArrayList<>();
        if (root == null) return list;

        Stack<TreeNode> stack = new Stack<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            list.add(cur.val);
            cur = cur.right; // 第一次取的是叶子节点，所以不会有右节点，后面会有右节点
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
        res.add(root.val);
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
