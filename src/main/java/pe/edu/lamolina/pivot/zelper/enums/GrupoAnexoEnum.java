package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum GrupoAnexoEnum {

    INGRESANTE("1", "ingresantes"),
    DPTO("2", "departamentos"),
    ACTIVIDADES("3", "actividades"),
    POSTGRADO("4", "postGrados"),
    OTROS("5", "otros"),
    TODO("0", "todos");

    private final String value;
    private final String descripcion;
    private static final Map<String, GrupoAnexoEnum> lookup = new HashMap();
    private static final Map<String, GrupoAnexoEnum> lookup2 = new HashMap();

    static {
        for (GrupoAnexoEnum d : GrupoAnexoEnum.values()) {
            lookup.put(d.getValue(), d);
            lookup2.put(d.getDescripcion(), d);
        }
    }

    private GrupoAnexoEnum(String value, String descripcion) {
        this.value = value;
        this.descripcion = descripcion;
    }

    public String getValue() {
        return value;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static GrupoAnexoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static GrupoAnexoEnum get2(String abbreviation) {
        return lookup2.get(abbreviation);
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
