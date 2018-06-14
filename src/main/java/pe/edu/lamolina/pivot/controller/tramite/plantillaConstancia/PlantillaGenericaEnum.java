package pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia;

import java.util.HashMap;
import java.util.Map;

public enum PlantillaGenericaEnum {

    NUMERO("numero", "__NUMERO__"),
    SERIE("serie", "__SERIE__"),
    NOMBRE("nombre", "__NOMBRE__"),
    CODIGOALUMNO("codigoalumno", "__CODIGOALUMNO__"),
    ALUMNO("alumno", "__ALUMNO__"),
    FACULTAD("facultad", "__FACULTAD__"),
    YEARINICIOCICLO("yeariniciociclo", "__YEARINICIOCICLO__"),
    YEARFINCICLO("yearfinciclo", "__YEARFINCICLO__"),
    MATRICULADO("matriculado", "__MATRICULADO__"),
    FECHA("fecha", "__FECHA__"),
    JEFEOFICINA("jefeoficina", "__JEFEOFICINA__");

    private final String value;
    private final String uppername;
    private static final Map<String, PlantillaGenericaEnum> lookup = new HashMap<>();
    private static final Map<String, PlantillaGenericaEnum> lookuppername = new HashMap<>();

    static {
        for (PlantillaGenericaEnum d : PlantillaGenericaEnum.values()) {
            lookup.put(d.getValue(), d);
            lookuppername.put(d.getUppername(), d);
        }
    }

    private PlantillaGenericaEnum(String value, String uppername) {
        this.value = value;
        this.uppername = uppername;
    }

    public String getValue() {
        return value;
    }

    public String getUppername() {
        return uppername;
    }
    
    public static PlantillaGenericaEnum get(String valor) {
        return lookup.get(valor);
    }
    
    public static PlantillaGenericaEnum getValor(String uppername) {
        return lookuppername.get(uppername);
    }

}
