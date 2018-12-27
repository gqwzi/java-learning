package space.pankui.jdk8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pankui
 * @date 2018-12-27
 * <pre>
 *
 * </pre>
 */
public class StreamMapExamples {

    public static void main(String[] args) {
        List<String> alpha = List.of("a", "b", "c", "d");

        //Before Java8
        List<String> alphaUpper = new ArrayList<>();
        for (String s : alpha) {
            alphaUpper.add(s.toUpperCase());
        }

        //[a, b, c, d]
        System.out.println(alpha);
        //[A, B, C, D]
        System.out.println(alphaUpper);

        // Java 8
        List<String> collect = alpha.stream().map(String::toUpperCase).collect(Collectors.toList());
        //[A, B, C, D]
        System.out.println(collect);

        // Extra, streams apply to any data type.
        List<Integer> num = Arrays.asList(1,2,3,4,5);
        List<Integer> collect1 = num.stream().map(n -> n * 2).collect(Collectors.toList());
        //[2, 4, 6, 8, 10]
        System.out.println(collect1);
    }
}
