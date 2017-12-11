package pe.edu.lamolina.pivot.zelper;

import pe.edu.lamolina.pivot.zelper.enums.GrupoAnexoEnum;

public class Laboratory {

    public static void main2(String[] args) {

        GrupoAnexoEnum gpoAnexoE = GrupoAnexoEnum.get2("ingresantes");
        System.out.println(gpoAnexoE.name());
        System.out.println(gpoAnexoE.getValue());

    }
}
