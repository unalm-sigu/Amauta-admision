package pe.edu.lamolina.pivot.zelper;

import pe.edu.lamolina.model.enums.GrupoAnexoEnum;
import pe.edu.lamolina.model.general.Persona;

public class Laboratory {

    public static void main2(String[] args) {

        GrupoAnexoEnum gpoAnexoE = GrupoAnexoEnum.get2("ingresantes");
        System.out.println(gpoAnexoE.name());
        System.out.println(gpoAnexoE.getValue());

        Persona p = new Persona();
        p.setPaterno("Postulante No Inscrito");
        p.setNombres("");

        System.out.println(p.getKey());

    }
}
