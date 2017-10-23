package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum DepartamentoAcademicoEstadoEnum {

    ACT("Activo"), CRE("Creado"), INA("Inactivo");

    private final String value;
    private static final Map<String, DepartamentoAcademicoEstadoEnum> lookup = new HashMap<>();

    static {
        for (DepartamentoAcademicoEstadoEnum d : DepartamentoAcademicoEstadoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private DepartamentoAcademicoEstadoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DepartamentoAcademicoEstadoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (DepartamentoAcademicoEstadoEnum d : DepartamentoAcademicoEstadoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
