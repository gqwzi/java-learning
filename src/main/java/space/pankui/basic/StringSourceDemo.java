package space.pankui.basic;

import java.io.UnsupportedEncodingException;

/**
 * @author pankui
 * @date 2018/10/21
 * <pre>
 *   String 方法使用
 * </pre>
 */
public class StringSourceDemo {

    public static void main(String[] args) throws UnsupportedEncodingException {



        /**
         *  字符串的长度也与编码相关，计算时通过右移来实现。
         *  如果是 LATIN-1 编码，则右移0位，数组长度即为字符串长度。
         *  而如果是 UTF16 编码，则右移1位，数组长度的二分之一为字符串长度。
         *
         *  0xFF
         *
         *  "啊"  汉字 不是 拉丁，字母是
         *
         * */
        String a1 = "ta啊";
        String emoj = "😂";
        // 这里 ta啊都是 LATIN-1编码
        System.out.println(a1.length()+" byte len="+a1.getBytes().length);
        // 数组长度的二分之一为字符串长度。因为表情是UTF16 编码
        System.out.println(emoj.length()+" byte len="+emoj.getBytes().length);

        boolean isLatin = a1.equals(new String(a1.getBytes("ISO-8859-1"), "ISO-8859-1"));
        System.out.println("isLatin="+isLatin);

        for (byte b : emoj.getBytes()) {
            System.out.println("byte val = "+b);
        }


        // charAt: 方法用于返回指定索引处的字符。索引范围为从0 到length() - 1
        //StringIndexOutOfBoundsException

        // UTF16 编码占用两个字节 所以这里是?
        System.out.println(emoj.charAt(1));

        System.out.println(a1.charAt(2));

        System.out.println(a1.codePointAt(1));
        System.out.println(emoj.codePointAt(1));

        System.out.println(emoj.codePointBefore(2));

        //codePointCount
        System.out.println(emoj.codePointCount(0,2));

    }


}
