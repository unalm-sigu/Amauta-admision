package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum OficinaEstadoEnum {

    ACT("Activo"), ANU("Anulado"),;

    private final String value;
    private static final Map<String, OficinaEstadoEnum> lookup = new HashMap<>();

    static {
        for (OficinaEstadoEnum d : OficinaEstadoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private OficinaEstadoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OficinaEstadoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (OficinaEstadoEnum d : OficinaEstadoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
