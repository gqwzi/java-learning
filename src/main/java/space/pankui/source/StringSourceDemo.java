package space.pankui.source;

import java.io.UnsupportedEncodingException;

/**
 * @author pankui
 * @date 2018/10/24
 * <pre>
 *   UTF-16 多出两个字节是因为UTF-6 有两个站位符，大小端
 * </pre>
 */
public class StringSourceDemo {

    public static void main(String[] args) throws UnsupportedEncodingException {

        String tt = "我喜欢?这个字符";
        System.out.println(tt.length());
        // UTF-8
        byte[] s1 = "啊啊啊".getBytes("UTF-8");
        System.out.println("UTF-8："+s1.length);

        byte[] s2 = "吂為".getBytes("UTF-16");
        System.out.println("UTF-16："+s2.length);

        byte[] s2_1 = "11".getBytes("UTF-16");
        System.out.println("UTF-16："+s2_1.length);


        byte[] s3 = "啊啊啊无服务费我访问服务".getBytes("ISO-8859-1");
        System.out.println("ISO-8859-1："+s3.length);

        byte[] s4 = "啊啊啊无服务费我访问服务".getBytes("GBK");
        System.out.println("GBK："+s4.length);


         char c1 = 'a';
        System.out.println();


        System.out.println(Integer.toBinaryString(1000));
        System.out.println(1000 >>> 8);
        System.out.println(1 >>> 8);

    }
}
