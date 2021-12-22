package leetcode.medium;

/**
 * @description: 加油站问题
 * @author: movesan
 * @create: 2020-10-24 23:15
 **/
public class GasStation {

    /**
     * 加油站  134：https://leetcode-cn.com/problems/gas-station/
     *
     * @param gas
     * @param cost
     * @return
     */
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int n = gas.length;

        int total = 0;
        int curr = 0;
        int start = 0;
        for (int i = 0; i < n; i++) {
            total += gas[i] - cost[i];
            curr += gas[i] - cost[i];
            // 如果当前油量小于0
            if (curr < 0) {
                // Pick up the next station as the starting one.
                start = i + 1;
                // Start with an empty tank.
                curr = 0;
            }
        }
        // 总路程中如果油量够用，那么肯定可以环形一周
        return total >= 0 ? start : -1;
    }
}
