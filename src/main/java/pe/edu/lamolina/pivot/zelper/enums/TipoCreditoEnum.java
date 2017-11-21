package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoCreditoEnum {

    FIJO("Fijo"), VAR("Variables");

    private final String value;
    private static final Map<String, TipoCreditoEnum> lookup = new HashMap<>();

    static {
        for (TipoCreditoEnum d : TipoCreditoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoCreditoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoCreditoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoCreditoEnum d : TipoCreditoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
