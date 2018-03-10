package pe.edu.lamolina.pivot.zelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.joda.time.DateTime;

public class Laboratory {

    public static void main2(String[] args) {

        DateTime inicio = new DateTime("2018-02-20");
        DateTime fin = new DateTime("2018-02-21");

        List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        numeros.remove(0);

        for (Integer numero : numeros) {
            System.out.println("numero::: " + numero);

        }

    }
}
