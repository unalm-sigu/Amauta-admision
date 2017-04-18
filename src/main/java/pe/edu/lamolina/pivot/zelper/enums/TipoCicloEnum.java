package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoCicloEnum {
    NIV("Nivelacion"), REG("Regular");

    private final String value;
    private static final Map<String, TipoCicloEnum> lookup = new HashMap<>();

    static {
        for (TipoCicloEnum d : TipoCicloEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoCicloEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoCicloEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoCicloEnum d : TipoCicloEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }

}
