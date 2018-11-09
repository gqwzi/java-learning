package space.pankui.jdk9;

import java.io.UnsupportedEncodingException;

/**
 * @author pankui
 * @date 2018/10/19
 * <pre>
 *
 * </pre>
 */
public class StringDemo {

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(Byte.SIZE);
        System.out.println(Character.SIZE);


        if (16 <= 0xF) {
            System.out.println("true");

        } else {
            System.out.println("false");
        }

        System.out.println(Integer.toBinaryString(256));

        System.out.println(Integer.toHexString(Integer.MAX_VALUE));
        System.out.println(Integer.MAX_VALUE);

        System.out.println(Integer.toHexString(16));
        System.out.println(Integer.toHexString(17));

        System.out.println(Integer.toHexString(21));
        System.out.println(Integer.toHexString(22));
        System.out.println(Integer.toHexString(23));
        System.out.println(Integer.toHexString(25));
        System.out.println(Integer.toHexString(127));

        System.out.println("########");
        System.out.println("Byte MIN ="+Byte.MIN_VALUE+",MAX="+Byte.MAX_VALUE);

        /** 注意这里不能使用 ISO8859-1,因为ISO8859-1编码的编码表中
         * 根本就没有包含汉字字符，当然也就无法通过"中".getBytes("ISO8859-1");
         * 来得到正确的“中”字在ISO8859-1中的编码值了，所以再通过new String()来还原就无从谈起了。
         *
         *
        */
        byte [] strByte = "abc$%^&*我😂😇囧".getBytes("UTF-8");
        for (int i = 0 ; i < strByte.length; i ++) {


            byte[] temp = {strByte[i]};
            String value = new String(temp,"UTF-8");
            // 有输出一些乱码 是因为 汉字和表情 UTF-8 占3个字节【三个字节一组分组】
            System.out.println("字符="+ value +",i="+i+", value="+strByte[i]);
        }
        System.out.println(System.getProperty("sun.jnu.encoding"));
        System.out.println(new String(strByte));
        //

        byte[] chinese = "中".getBytes("UTF-8");
        byte [] newChinese = new byte[chinese.length];
        for (byte i = 0 ; i < chinese.length ; i++) {
            // 从这里可以看出汉字占用3个字节
            // [e7 94 a8] [e6 88 b7] [e5 90 8d]
            System.out.println("chinese="+chinese[i]);
            newChinese[i] = chinese [i];
        }

        System.out.println(new String(newChinese));
    }
}
