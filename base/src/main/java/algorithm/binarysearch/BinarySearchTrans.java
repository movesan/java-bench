package algorithm.binarysearch;

/**
 * @description: 二分查找变形
 * @author: movesan
 * @create: 2020-04-26 20:38
 **/
public class BinarySearchTrans {

    /**
     * 给出一个数：n，查找 n 在数组中的位置：[7,8,9,10,1,2,3,4,5]
     * @param arr
     * @param n
     * @return
     */
    public static int search(int[] arr, int n) {
        if (arr.length == 0) return -1;
        int first = arr[0];
        int last = arr[arr.length-1];
        int lo = 0;
        int hi = arr.length-1;
        while (lo <= hi) {
            int mid = lo + ((hi - lo) & 1);
            if (arr[mid] > n) {
                if (first > n) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            } else if (arr[mid] < n) {
                if (last < n) {
                    hi = mid - 1;
                } else {
                    lo = mid + 1;
                }
            } else {
                return mid;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {7,8,9,10,1,2,3,4,5};
        System.out.print(search(arr, 3));
    }
}
