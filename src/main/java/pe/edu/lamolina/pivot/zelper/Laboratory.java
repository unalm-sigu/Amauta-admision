package pe.edu.lamolina.pivot.zelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pe.edu.lamolina.pivot.controller.academico.calculonotas.Fraxtion;

public class Laboratory {

    public static void main2(String[] args) {

        List<Fraxtion> frs = new ArrayList();
        frs.add(new Fraxtion("12.4"));
        frs.add(new Fraxtion("-12.4"));
        frs.add(new Fraxtion("2.4"));
        frs.add(new Fraxtion("-34.4"));

        Collections.sort(frs, new Fraxtion.OrdenReverso());
        for (Fraxtion fr : frs) {
            System.out.println(fr);
        }

    }
}
