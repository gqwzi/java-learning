package space.pankui.jdk8;

import java.util.Optional;

/**
 * @author pankui
 * @date 2019-03-01
 * <pre>
 *
 * </pre>
 */
public class OptionalExample01 {

    public static void main(String[] args) {

        User user = new User("test");
        User userNull = new User(null);
        System.out.println(OptionalExample01.getName(user));
        System.out.println(OptionalExample01.getName(userNull));

        System.out.println("########3 ");

        System.out.println(OptionalExample01.getName2(user));
        System.out.println(OptionalExample01.getName2(userNull));
    }



    public static String getName(User u) {
        if (u == null) {
            return "Unknown";
        }
        return u.name;
    }

    /**
     * 替换成下面
     *
     * @param user
     * @return
     */
    public static String getName2(User user){
        return Optional.ofNullable(user)
                .map(user1 -> user1.name)
                .orElse(null);

    }
}


class User {

    String name;

    public String getName(){
        return name;
    }

    User(String name) {
        this.name = name;
    }
}
