package pe.edu.lamolina.pivot.zelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Laboratory {

    public static void main22(String[] args) {
        List<Integer> lista = new ArrayList();
        List<Integer> rev = new ArrayList();
        List<Integer> poste = new ArrayList();
        List<Integer> compa = new ArrayList();
        List<String> ver = new ArrayList();

        Random r = new Random();

        for (int i = 0; i < 1000; i++) {
            int x = r.nextInt(25) + 10;
            compa.add(x);

            ver.add(rev.toString());
            if (rev.contains(x)) {
                poste.add(x);
                continue;
            }
            if (!poste.isEmpty()) {
                int y = poste.get(0);
                if (!rev.contains(y) && y != x) {
                    poste.remove(0);
                    rev.add(y);
                    lista.add(y);
                    if (rev.size() > 5) {
                        rev.remove(0);
                    }
                }
            }
            rev.add(x);
            lista.add(x);

            if (rev.size() > 5) {
                rev.remove(0);
            }
        }
        if (!poste.isEmpty()) {
            lista.addAll(poste);
        }

        int tope = 0;
        for (int i = 0; i < lista.size(); i++) {
            System.out.println(lista.get(i) + "-" + compa.get(i) + " " + ver.get(i));
            tope++;
            if (tope > 17) {
                //System.out.println("");
                tope = 0;
            }
        }
        System.out.println("");

        Collections.sort(lista);
        Collections.sort(compa);
        tope = 0;
        for (int i = 0; i < lista.size(); i++) {
            System.out.println(lista.get(i) + "-" + compa.get(i) + " " + ver.get(i));
            tope++;
            if (tope > 17) {
                //System.out.println("");
                tope = 0;
            }
        }
    }
}
