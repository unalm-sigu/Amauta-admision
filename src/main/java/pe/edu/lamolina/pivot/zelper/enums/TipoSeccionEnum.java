package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoSeccionEnum {

    TEO("Teória"), PRAC("Práctica");

    private final String value;
    private static final Map<String, TipoSeccionEnum> lookup = new HashMap<>();

    static {
        for (TipoSeccionEnum d : TipoSeccionEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoSeccionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoSeccionEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoSeccionEnum d : TipoSeccionEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
