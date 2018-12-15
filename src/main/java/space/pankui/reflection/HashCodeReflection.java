package space.pankui.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author pankui
 * @date 2018-12-15
 * <pre>
 *
 * </pre>
 */
public class HashCodeReflection {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<String> stringClass = String.class;

        Method hashCodeMethod = stringClass.getDeclaredMethod("hashCode");

        String str= "hello, world";
        Object hashCode= hashCodeMethod.invoke(str);
        System.out.println("hashCode:"+ hashCode);

        System.out.println(str.hashCode());

    }
}
