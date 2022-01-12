package leetcode.binarytree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @description: 层序遍历
 * @author: movesan
 * @create: 2020-09-17 17:29
 **/
public class LevelOrderTraversal {

    /**
     * 二叉树层次遍历
     * @param root
     * @return
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) return res;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (queue.size() > 0) {
            int levelSize = queue.size();
            List<Integer> levelList = new ArrayList<>();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                levelList.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            res.add(levelList);
        }
        return res;
    }

    /**
     * 二叉树锯齿形层次遍历
     * @param root
     * @return
     */
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) return res;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        // 遍历方向标识
        boolean orderFlag = true;
        while (queue.size() > 0) {
            int levelSize = queue.size();
            LinkedList<Integer> levelList = new LinkedList<>();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (orderFlag) {
                    levelList.add(node.val);
                } else {
                    levelList.addFirst(node.val);
                }
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            res.add(levelList);
            orderFlag = !orderFlag;
        }
        return res;
    }

    @Test
    public void test() {
        TreeNode node = TreeNode.getTree();
//        System.out.println(levelOrder(node));
        System.out.println(zigzagLevelOrder(node));
    }
}
