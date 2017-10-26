package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum DocenteEstadoEnum {

    ACT("Activo"), INA("Inactivo");

    private final String value;
    private static final Map<String, DocenteEstadoEnum> lookup = new HashMap<>();

    static {
        for (DocenteEstadoEnum d : DocenteEstadoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private DocenteEstadoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DocenteEstadoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (DocenteEstadoEnum d : DocenteEstadoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
