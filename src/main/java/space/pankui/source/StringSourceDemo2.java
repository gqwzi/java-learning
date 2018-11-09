package space.pankui.source;

/**
 * @author pankui
 * @date 2018/10/28
 * <pre>
 *
 * </pre>
 */
public class StringSourceDemo2 {

    public static void main(String[] args) {
        String s = new String("ab");
        System.out.println(s.indexOf(98));


        String strA  =   " abc " ;
        String strB  =   " abc " ;
        String strAA  =   new  String( " abc " );
        String strBB  =   new  String( " abc " );
        System.out.println(strA  ==  strB);
        System.out.println(strAA  ==  strBB);


    }
}


