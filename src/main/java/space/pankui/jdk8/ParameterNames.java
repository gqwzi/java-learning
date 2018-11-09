package space.pankui.jdk8;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author pankui
 * @date 2018/7/12
 * <pre>
 *
 * </pre>
 */
public class ParameterNames {
    public static void main(String[] args) throws Exception {
        Method method = ParameterNames.class.getMethod( "main", String[].class );
        for( final Parameter parameter: method.getParameters() ) {
            System.out.println( "Parameter: " + parameter.getName() );

            //来验证参数名是不是可用。
            System.out.println(parameter.isNamePresent());
        }


        String a = "abc";
        String b = a.replace("a","A");

        System.out.println("a="+a);
        System.out.println("b="+b);


    }

}



