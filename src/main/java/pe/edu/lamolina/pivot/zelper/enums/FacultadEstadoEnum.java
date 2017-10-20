package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum FacultadEstadoEnum {

    ACT("Activo"), CRE("Creado"), DES("Desactivado");

    private final String value;
    private static final Map<String, FacultadEstadoEnum> lookup = new HashMap<>();

    static {
        for (FacultadEstadoEnum d : FacultadEstadoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private FacultadEstadoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FacultadEstadoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (FacultadEstadoEnum d : FacultadEstadoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
