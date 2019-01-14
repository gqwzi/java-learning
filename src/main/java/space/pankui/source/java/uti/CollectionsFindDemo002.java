package space.pankui.source.java.uti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author pankui
 * @date 2019-01-14
 * <pre>
 *
 * </pre>
 */
public class CollectionsFindDemo002 {

    public static void main(String[] args) {
        Integer[]  ints = new Integer[]{5,13,19,21,37,56,64,75,80,88,92};
        ArrayList<Integer> bkList1 = new ArrayList<>(Arrays.asList(ints));

        //before reverse
        System.out.println("original list:	"+Arrays.toString(ints));

        //reverse list
        Collections.reverse(bkList1);
        System.out.println("reversed list:	"+bkList1);

        //sort the list
        ArrayList<Integer> bkList2 = new ArrayList<>(Arrays.asList(ints));
        Collections.sort(bkList2);
        System.out.println("sorted list:	"+bkList2);

        //binary search with included key
        int index = Collections.binarySearch(bkList2, 21);
        System.out.println("search 21:	"+index);

        //binary search with non-included key
        int Index2 = Collections.binarySearch(bkList2, 85);
        System.out.println("search 85:	"+Index2);
        if (Index2 < 0) {
            //insertposition -Index2-1
            bkList2.add(-Index2 - 1, 85);
        }
        System.out.println("after insert:	"+bkList2);
    }


}
