package pe.edu.lamolina.pivot.zelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.miscelanea.Commutator;

public class Laboratory {

    public static void main666(String[] args) {

        Map<Long, Object> mapDias = new LinkedHashMap();
        mapDias.put(1L, 2);
        mapDias.put(2L, 3);
        mapDias.put(4L, 1);
        mapDias.put(6L, 2);

        List<Map<Long, Object>> busquedas = Commutator.create(mapDias);

        for (Map<Long, Object> busqueda : busquedas) {
            int total = 0;
            String dias = "";
            String horas = "";
            for (Map.Entry<Long, Object> entry : busqueda.entrySet()) {
                total += (Integer) entry.getValue();
                dias += dias.equals("") ? "" : "-";
                dias += entry.getKey();
                horas += horas.equals("") ? "" : "+";
                horas += entry.getValue();
            }
            System.out.println(total + " :::: " + horas + " :::: " + dias);
        }

    }
}
