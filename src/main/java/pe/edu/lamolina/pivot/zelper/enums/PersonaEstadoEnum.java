package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum PersonaEstadoEnum {

    ACT("Activo"), INA("Inactivo");

    private final String value;
    private static final Map<String, PersonaEstadoEnum> lookup = new HashMap<>();

    static {
        for (PersonaEstadoEnum d : PersonaEstadoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private PersonaEstadoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PersonaEstadoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (PersonaEstadoEnum d : PersonaEstadoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
