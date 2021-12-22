import java.util.Arrays;

/**
 * @description:
 * @author: movesan
 * @create: 2020-10-21 20:31
 **/
public class Test {
    public static void main(String[] args) {
//        int[] arr = new int[]{-1,2,4,5,-2,3};
//        System.out.print(test(arr));
        Test m = new Test();
        m.doIt();
    }

    public static int test(int[] arr) {
        // -1,2,4,5,-2,3
        int len = arr.length;
        // 第i 位最大值
        int[] dp = new int[len];
        dp[0] = arr[0];
        for (int i=1; i<len; i++) {
            int max = 0;
            for (int j=i; j >= 0; j--) {
                if (arr[j] < 0) {
                    break;
                }
                max = arr[j] + max;
            }
            dp[i] = max;
            if (arr[i] <= 0) {
                dp[i] = dp[i - 1];
            } else {
                dp[i] = dp[i - 1] + arr[i];
            }
        }
        System.out.println(Arrays.toString(dp));
        return dp[len - 1];
    }

    public final int value = 4;
    public void doIt() {
        int value = 6;
        Runnable r = new Runnable() {
            public final int value = 5;
            public void run(){
                int value = 10;
                System.out.println(this.value);
            }
        };
        r.run();
    }

}
