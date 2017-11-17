package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoCursoCurriculaEnum {

    OBL("Obligatorio"),
    GEN("Generales"),
    ELC("Electivo Carrera"),
    ELF("Electivo Facultad"),
    ELE("Electivo Libre");

    private final String value;
    private static final Map<String, TipoCursoCurriculaEnum> lookup = new HashMap<>();

    static {
        for (TipoCursoCurriculaEnum d : TipoCursoCurriculaEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoCursoCurriculaEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoCursoCurriculaEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoCursoCurriculaEnum d : TipoCursoCurriculaEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
