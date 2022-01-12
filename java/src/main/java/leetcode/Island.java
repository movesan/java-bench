package leetcode;

import org.junit.Test;

/**
 * @description: 岛屿题
 * @author: movesan
 * @create: 2020-10-24 17:54
 **/
public class Island {

    /** =============  最大岛屿数量  200:https://leetcode-cn.com/problems/number-of-islands/  =============  **/

    /**
     * DFS 解法
     *
     * @return
     */
    @Test
    public void numIslandsTest() {
        char[][] param = new char[][]{
                {'1', '0', '1', '0', '0'},
                {'1', '0', '1', '1', '1'},
                {'1', '0', '1', '1', '1'},
                {'1', '0', '0', '1', '0'}
        };
        System.out.println(numIslands(param));
    }

    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int row = grid.length;
        int col = grid[0].length;
        int num = 0;
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                if (grid[r][c] == '1') {
                    num++;
                    dfs(grid, r, c);
                }
            }
        }

        return num;
    }

    public void dfs(char[][] grid, int r, int c) {
        int row = grid.length;
        int col = grid[0].length;

        // 停止搜索
        if (r < 0 || c < 0 || r >= row || c >= col || grid[r][c] == '0') {
            return;
        }
        // 重置为0，避免影响下次搜索
        grid[r][c] = '0';
        dfs(grid, r - 1, c);
        dfs(grid, r + 1, c);
        dfs(grid, r, c - 1);
        dfs(grid, r, c + 1);
    }
}
