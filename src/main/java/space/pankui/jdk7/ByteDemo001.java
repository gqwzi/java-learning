package space.pankui.jdk7;

/**
 * @author pankui
 * @date 30/06/2018
 * <pre>
 *
 * </pre>
 */
public class ByteDemo001 {

    public static void main(String[] args) {

        // New
        int a = 0b0111_00000_11111_00000_10101_01010_10;
        // New
        short b = (short)0b01100_00000_11111_0;
        // New
        byte c = (byte)0B0000_0001;
        // New
        byte b1 = 0b00100001;
        // Old
        byte b2 = 0x21;
        // Old
        byte b3 = 33;


        System.out.println(a);

        System.out.println(b);

        System.out.println(c);

        System.out.println(b1);

        System.out.println(b2);

        System.out.println(b3);
    }
}
