package pe.edu.lamolina.pivot.zelper;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.enums.EstadoAporteEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.VisibleEnum;
import pe.edu.lamolina.model.inscripcion.Taller;

public class Laboratory {

    private static final String fmt = "  %11s:  %s %s%n";

    public static void main666(String[] args) {
        Aporte a = new Aporte();
        a.setCodigo("01");
        a.setNombre("Aporte Semestral");
        a.setExonerable(Boolean.TRUE);
        a.setEstadoEnum(EstadoAporteEnum.ACT);

        ObjectNode apoJson = JsonHelper.createJson(a, JsonNodeFactory.instance, true, new String[]{"*"});
        System.out.println(apoJson.toString());

        Taller ta = new Taller();
        ta.setAforo(23);
        ta.setDescripcion("Acotados");
        ta.setVisible(VisibleEnum.SI);
        ta.setEstado(EstadoEnum.ACT);
        ta.setInscritos(343);

        apoJson = JsonHelper.createJson(ta, JsonNodeFactory.instance, true, new String[]{"*"});
        System.out.println(apoJson.toString());

    }

}
