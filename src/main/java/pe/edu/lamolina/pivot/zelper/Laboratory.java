package pe.edu.lamolina.pivot.zelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.Curso;

public class Laboratory {

    public static void main666(String[] args) {
        Casa casa = new Casa(1, "Madera", "Solido");

        List<Casa> l = Arrays.asList(
                new Casa(5, "Madera", "Solido"),
                new Casa(2, "Madera", "Solido"),
                new Casa(6, "Madera", "Solido"),
                new Casa(4, "Madera", "Solido")
        );
        List<Casa> m = Arrays.asList(
                new Casa(5, "Madera", "Solido"),
                new Casa(6, "Madera", "Solido"),
                new Casa(7, "Madera", "Solido")
        );
        List<Casa> apr = Arrays.asList(
                new Casa(5, "Madera", "Solido"),
                new Casa(6, "Madera", "Solido"),
                new Casa(7, "Madera", "Solido"),
                new Casa(8, "Madera", "Solido")
        );
        Map<Integer, List<Casa>> mapEquivalentes = new HashMap<>();
        mapEquivalentes.put(1, l);
        mapEquivalentes.put(2, m);
        List<String> a = Arrays.asList("A", "B", "C");
        List<String> b = Arrays.asList("A", "B", "C");
        String c = "C";
        for (Integer ids : mapEquivalentes.keySet()) {
            Boolean res = mapEquivalentes.get(ids).stream().allMatch(x -> apr.stream().anyMatch(y -> y.getId() == x.getId()));
            System.err.println("hay " + res + " - " + ids);
        }
        if (a.contains(c)) {

            System.err.println("hay");
        } else {

            System.err.println(" no hay");
        }

    }

}

class Casa {

    Integer id;
    String materialCocina;
    String materialPiso;

    public Casa(Integer id, String materialCocina, String materialPiso) {
        this.id = id;
        this.materialCocina = materialCocina;
        this.materialPiso = materialPiso;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaterialCocina() {
        return materialCocina;
    }

    public void setMaterialCocina(String materialCocina) {
        this.materialCocina = materialCocina;
    }

    public String getMaterialPiso() {
        return materialPiso;
    }

    public void setMaterialPiso(String materialPiso) {
        this.materialPiso = materialPiso;
    }

}
