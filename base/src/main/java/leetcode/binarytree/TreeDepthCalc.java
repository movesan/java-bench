package leetcode.binarytree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import javafx.util.Pair;
import org.junit.Test;

/**
 * @description: 深度计算
 * @author: movesan
 * @create: 2020-09-18 10:12
 **/
public class TreeDepthCalc {

    /**
     * 二叉树的最大深度（DFS）
     * @param root
     * @return
     */
    public int maxDepthDFS(TreeNode root) {
        if (root == null) return 0;

        int depth = 0;
        Stack<Pair<TreeNode, Integer>> stack = new Stack<>();
        stack.push(new Pair<>(root, 1));
        while (!stack.isEmpty()) {
            Pair<TreeNode, Integer> pair = stack.pop();
            TreeNode node = pair.getKey();
            Integer val = pair.getValue();
            depth = Math.max(depth, val);
            if (node.right != null) {
                stack.push(new Pair<>(node.right, val + 1));
            }
            if (node.left != null) {
                stack.push(new Pair<>(node.left, val + 1));
            }
        }
        return depth;
    }

    /**
     * 二叉树的最大深度（BFS）
     * @param root
     * @return
     */
    public int maxDepthBFS(TreeNode root) {
        if (root == null) return 0;

        int depth = 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (queue.size() > 0) {
            depth++;
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
        }
        return depth;
    }

    /**
     * 二叉树的最小深度（DFS）
     * @param root
     * @return
     */
    public int minDepthDFS(TreeNode root) {
        if (root == null) return 0;
        if (root.left == null && root.right == null) return 1;

        int depth = Integer.MAX_VALUE;
        Stack<Pair<TreeNode, Integer>> stack = new Stack<>();
        stack.push(new Pair<>(root, 1));
        while (!stack.isEmpty()) {
            Pair<TreeNode, Integer> pair = stack.pop();
            TreeNode node = pair.getKey();
            Integer level = pair.getValue();
            if (node.left != null) {
                stack.push(new Pair<>(node.left, level + 1));
            }
            if (node.right != null) {
                stack.push(new Pair<>(node.right, level + 1));
            }
            if (node.left == null && node.right == null) {
                depth = Math.min(depth, level);
            }
        }
        return depth;
    }

    /**
     * 二叉树的最小深度（BFS）
     * @param root
     * @return
     */
    public int minDepthBFS(TreeNode root) {
        if (root == null) return 0;

        int depth = 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (queue.size() > 0) {
            depth++;
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
                if (node.left == null && node.right == null) {
                    return depth;
                }
            }
        }
        return depth;
    }

    @Test
    public void maxDepthTest() {
        System.out.println("DFS最大深度：" + maxDepthDFS(TreeNode.getTree()));
        System.out.println("BFS最大深度：" + maxDepthBFS(TreeNode.getTree()));
    }

    @Test
    public void minDepthTest() {
        System.out.println("DFS最小深度：" + minDepthDFS(TreeNode.getTree()));
        System.out.println("BFS最小深度：" + minDepthBFS(TreeNode.getTree()));
    }
}
