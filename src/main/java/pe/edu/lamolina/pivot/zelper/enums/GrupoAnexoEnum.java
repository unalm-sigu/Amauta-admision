package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum GrupoAnexoEnum {

    INGRESANTE("1"), DPTO("2"), ACTIVIDADES("3"), POSTGRADO("4"), OTROS("5"),;

    private final String value;
    private static final Map<String, GrupoAnexoEnum> lookup = new HashMap<>();

    static {
        for (GrupoAnexoEnum d : GrupoAnexoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private GrupoAnexoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GrupoAnexoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (GrupoAnexoEnum d : GrupoAnexoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
