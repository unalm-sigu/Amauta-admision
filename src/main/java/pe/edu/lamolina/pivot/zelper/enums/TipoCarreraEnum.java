package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoCarreraEnum {

    SEM("Semestral"), DOC("Doctorado"), MAE("Maestria"), PMA("Especial");

    private final String value;
    private static final Map<String, TipoCarreraEnum> lookup = new HashMap<>();

    static {
        for (TipoCarreraEnum d : TipoCarreraEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoCarreraEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoCarreraEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoCarreraEnum d : TipoCarreraEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
