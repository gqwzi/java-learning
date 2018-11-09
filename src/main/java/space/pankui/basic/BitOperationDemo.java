package space.pankui.basic;

/**
 * @author pankui
 * @date 2018/10/15
 * <pre>
 *
 * </pre>
 */
public class BitOperationDemo {

    public static void main(String[] args) {
        //运行结果是20
        System.out.println(5<<2);

        //运行结果是1
        System.out.println(5>>2);

        //结果是0
        System.out.println(5>>3);

        //结果是-1
        System.out.println(-5>>3);

        //结果是536870911
        System.out.println(-5>>>3);

        System.out.println(5 & 3);

        System.out.println(Integer.toBinaryString(-5));


        System.out.println();

    }
}
