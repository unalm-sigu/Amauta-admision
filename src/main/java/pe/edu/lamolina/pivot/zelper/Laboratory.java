package pe.edu.lamolina.pivot.zelper;

import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.enums.GrupoAnexoEnum;

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
