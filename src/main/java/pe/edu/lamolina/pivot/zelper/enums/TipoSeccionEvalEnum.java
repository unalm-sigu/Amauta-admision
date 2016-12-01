package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoSeccionEvalEnum {

    TEO("Teoría"), PRAC("Práctica");

    private final String value;
    private static final Map<String, TipoSeccionEvalEnum> lookup = new HashMap<>();

    static {
        for (TipoSeccionEvalEnum d : TipoSeccionEvalEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoSeccionEvalEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoSeccionEvalEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoSeccionEvalEnum d : TipoSeccionEvalEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
