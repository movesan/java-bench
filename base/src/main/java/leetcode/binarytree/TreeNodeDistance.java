package leetcode.binarytree;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 节点距离问题
 * @author: movesan
 * @create: 2020-10-20 10:29
 **/
public class TreeNodeDistance {

    List<Integer> res = new ArrayList<>();

    /**
     * 二叉树中所有距离为 K 的结点
     * @param root
     * @param target
     * @param K
     * @return
     */
    public List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        dfs(root, target, K);
        return res;
    }

    /**
     * 每个节点到 target 节点的距离
     *
     * 思路:
     *
     * 如果 target 节点在 root 节点的左子树中，且 target 节点深度为 3，那所有 root 节点右子树中深度为 K - 3 的节点到 target 的距离就都是 K。
     *
     * 算法:
     *
     * 深度优先遍历所有节点。定义方法 dfs(node)，这个函数会返回 node 到 target 的距离。在 dfs(node) 中处理下面四种情况：
     *
     * 如果 node == target，把子树中距离 target 节点距离为 K 的所有节点加入答案。
     *
     * 如果 target 在 node 左子树中，假设 target 距离 node 的距离为 L+1，找出右子树中距离 target 节点 K - L - 1 距离的所有节点加入答案。
     *
     * 如果 target 在 node 右子树中，跟在左子树中一样的处理方法。
     *
     * 如果 target 不在节点的子树中，不用处理。
     *
     * 实现的算法中，还会用到一个辅助方法 subtree_add(node, dist)，这个方法会将子树中距离节点 node K - dist 距离的节点加入答案。
     *
     *
     * @param node
     * @param target
     * @param K
     * @return
     */
    private int dfs(TreeNode node, TreeNode target, int K) {
        if (node == null) {
            return -1;
        } else if (node == target) {
            addSub(node, 0, K);
            return 1;
        } else {
            int L = dfs(node.left, target, K);
            int R = dfs(node.right, target, K);
            if (L != -1) {
                if (L == K) {
                    res.add(node.val);
                }
                addSub(node.right, L + 1, K);
                return L + 1;
            } else if (R != -1) {
                if (R == K) {
                    res.add(node.val);
                }
                addSub(node.left, R + 1, K);
                return R + 1;
            } else {
                return -1;
            }
        }
    }

    private void addSub(TreeNode node, int dist, int K) {
        if (node == null) return;
        if (dist == K)
            res.add(node.val);
        else {
            addSub(node.left, dist + 1, K);
            addSub(node.right, dist + 1, K);
        }
    }


}
