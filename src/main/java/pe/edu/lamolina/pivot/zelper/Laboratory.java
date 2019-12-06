package pe.edu.lamolina.pivot.zelper;

import java.util.Arrays;
import java.util.List;

public class Laboratory {

    public static void main666(String[] args) {

        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        Integer sum = integers.stream()
                .reduce(0, Integer::sum);

        System.out.println(sum);

    }

}
