package space.pankui.jdk9;

/**
 * @author pankui
 * @date 2018/10/20
 * <pre>
 *  补码
 * </pre>
 */
public class ComplementDemo {

    public static void main(String[] args) {
        // int 占32 bit
        // 1111 1111 1111 1111 1111 1111 1111 1110
        System.out.println(Integer.toBinaryString(-2));

        System.out.println(Integer.toBinaryString(2));

        byte b = 2;
        int i = b & 0xff;

        System.out.println(i);

        byte c = -2;

        // 254
        int j = c ;// & 0xff;

        System.out.println(j);

        System.out.println(Integer.toHexString(254));
    }
}
