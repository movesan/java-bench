package algorithm.sorting.quick;

import algorithm.sorting.insertion.Insertion;

import static algorithm.sorting.utils.SortingHelper.eq;
import static algorithm.sorting.utils.SortingHelper.exch;
import static algorithm.sorting.utils.SortingHelper.isSorted;
import static algorithm.sorting.utils.SortingHelper.less;
import static algorithm.sorting.utils.SortingHelper.show;

/**
 * @description: 快速排序：优化版本 optimized 3-way quicksort
 * @author: movesan
 * @create: 2020-01-10 15:22
 **/
public class QuickBentleyMcIlroy {

    // cutoff to insertion sort, must be >= 1
    private static final int INSERTION_SORT_CUTOFF = 8;

    // cutoff to median-of-3 partitioning
    private static final int MEDIAN_OF_3_CUTOFF = 40;

    // This class should not be instantiated.
    private QuickBentleyMcIlroy() { }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
    public static void sort(Comparable[] a) {
        sort(a, 0, a.length - 1);
    }

    private static void sort(Comparable[] a, int lo, int hi) {
        int n = hi - lo + 1;

        // cutoff to insertion sort
        if (n <= INSERTION_SORT_CUTOFF) {
            Insertion.sort(a, lo, hi);
            return;
        }

        // use median-of-3 as partitioning element
        else if (n <= MEDIAN_OF_3_CUTOFF) {
            int m = median3(a, lo, lo + n/2, hi);
            exch(a, m, lo);
        }

        // use Tukey ninther as partitioning element
        else  {
            int eps = n/8;
            int mid = lo + n/2;
            int m1 = median3(a, lo, lo + eps, lo + eps + eps);
            int m2 = median3(a, mid - eps, mid, mid + eps);
            int m3 = median3(a, hi - eps - eps, hi - eps, hi);
            int ninther = median3(a, m1, m2, m3);
            exch(a, ninther, lo);
        }

        // Bentley-McIlroy 3-way partitioning
        int i = lo, j = hi+1;
        int p = lo, q = hi+1;
        Comparable v = a[lo];
        while (true) {
            while (less(a[++i], v))
                if (i == hi) break;
            while (less(v, a[--j]))
                if (j == lo) break;

            // pointers cross
            if (i == j && eq(a[i], v))
                exch(a, ++p, i);
            if (i >= j) break;

            exch(a, i, j);
            if (eq(a[i], v)) exch(a, ++p, i);
            if (eq(a[j], v)) exch(a, --q, j);
        }


        i = j + 1;
        for (int k = lo; k <= p; k++)
            exch(a, k, j--);
        for (int k = hi; k >= q; k--)
            exch(a, k, i++);

        sort(a, lo, j);
        sort(a, i, hi);
    }

    // return the index of the median element among a[i], a[j], and a[k]
    private static int median3(Comparable[] a, int i, int j, int k) {
        return (less(a[i], a[j]) ?
                (less(a[j], a[k]) ? j : less(a[i], a[k]) ? k : i) :
                (less(a[k], a[j]) ? j : less(a[k], a[i]) ? k : i));
    }

    /**
     * Reads in a sequence of strings from standard input; quicksorts them
     * (using an optimized version of quicksort);
     * and prints them to standard output in ascending order.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
//        String[] a = StdIn.readAllStrings();
        String[] a = {"S", "O", "R", "T", "E", "X", "A", "M", "P", "L", "E"};
        QuickBentleyMcIlroy.sort(a);
        assert isSorted(a);
        show(a);
    }
}
