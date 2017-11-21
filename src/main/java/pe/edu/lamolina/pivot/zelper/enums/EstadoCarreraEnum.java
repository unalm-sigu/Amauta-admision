package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum EstadoCarreraEnum {

    ACT("Activo"), CRE("Creado"), INA("Inactivo"), RES("Resolución");

    private final String value;
    private static final Map<String, EstadoCarreraEnum> lookup = new HashMap<>();

    static {
        for (EstadoCarreraEnum d : EstadoCarreraEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private EstadoCarreraEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoCarreraEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (EstadoCarreraEnum d : EstadoCarreraEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
