package algorithm.binarysearch;

/**
 * @description: 二分查找 练习版
 * @author: movesan
 * @create: 2020-04-16 14:14
 **/
public class BinarySearchQuizzes {

    /**
     * 查找等于给定值
     *
     * @param arr
     * @param val
     * @return
     */
    private static int binarySearch(int[] arr, int val) {
        int lo = 0;
        int hi = arr.length - 1;
        while (lo <= hi) {
            int mid = lo + ((hi - lo) >> 1);
            if (arr[mid] < val) {
                // 在右区间查找
                lo = mid + 1;
            } else if (arr[mid] > val) {
                // 在左区间查找
                hi = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    /**
     * 查找第一个等于给定值的下标
     *
     * @param arr
     * @param val
     * @return
     */
    private static int binarySearchFirstEquals(int[] arr, int val) {
        int lo = 0;
        int hi = arr.length - 1;
        while (lo <= hi) {
            int mid = lo + ((hi - lo) >> 1);
            if (arr[mid] < val) {
                lo = mid + 1;
            } else if (arr[mid] > val) {
                hi = mid - 1;
            } else {
                if (mid == 0 || arr[mid - 1] != val) {
                    return mid;
                } else {
                    hi = mid - 1;
                }
            }
        }
        return -1;
    }

    /**
     * 查找第一个大于给定值的下标
     *
     * @param arr
     * @param val
     * @return
     */
    private static int binarySearchFirstGreater(int[] arr, int val) {
        int lo = 0;
        int hi = arr.length - 1;
        while (lo <= hi) {
            int mid = lo + ((hi - lo) >> 1);
            if (arr[mid] <= val) {
                lo = mid + 1;
            } else {
                if (mid == 0 || arr[mid - 1] <= val) {
                    return mid;
                } else {
                    hi = mid - 1;
                }
            }
        }
        return -1;
    }

    /**
     * 查找最后一个小于等于给定值的元素
     *
     * @param arr
     * @param val
     * @return
     */
    private static int binarySearchLastSmaller(int[] arr, int val) {
        int lo = 0;
        int hi = arr.length - 1;
        while (lo <= hi) {
            int mid = lo + ((hi - lo) >> 1);
            if (arr[mid] > val) {
                hi = mid - 1;
            } else {
                if ((mid == arr.length - 1) || (arr[mid + 1] > val)) {
                    return mid;
                } else {
                    lo = mid + 1;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 6, 7, 8, 8, 8, 9};
        System.out.println("下标为：" + BinarySearchQuizzes.binarySearchLastSmaller(arr, 7));
    }
}
