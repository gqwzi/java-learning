package space.pankui.jdk8;

import java.util.Optional;

/**
 * @author pankui
 * @date 2019-03-01
 * <pre>
 *
 * </pre>
 */
public class OptionalExample02 {

    public static void main(String[] args) {
        try {
            System.out.println(OptionalExample02.getChampionName(null));
        }catch (Exception e) {

        }

        System.out.println(OptionalExample02.getChampionName2(null));
    }

    public static String getChampionName(Competition comp) throws IllegalArgumentException {
        if (comp != null) {
            CompResult result = comp.getResult();
            if (result != null) {
                User champion = result.getChampion();
                if (champion != null) {
                    return champion.getName();
                }
            }
        }
        throw new IllegalArgumentException("The value of param comp isn't available.");
    }

    /**
     * 优化成下面
     * */
    public static String getChampionName2(Competition comp) throws IllegalArgumentException {
        return Optional.ofNullable(comp)
                .map(Competition::getResult)
                .map(CompResult::getChampion)
                .map(User::getName)
                .orElseThrow(()-> new IllegalArgumentException("The value of param comp isn't available."));
    }
}

class CompResult {
    User user;

    public User getChampion() {
        return user;
    }
}

class Competition {

    CompResult result;

    public CompResult getResult() {
        return result;
    }
}

