package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum EstadoCursoCachimboEnum {

    ACT("Activo"), CER("Cerrado"), CRE("Creado"), INA("Inactivo"), ANU("Anulado");

    private final String value;
    private static final Map<String, EstadoCursoCachimboEnum> lookup = new HashMap<>();

    static {
        for (EstadoCursoCachimboEnum d : EstadoCursoCachimboEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private EstadoCursoCachimboEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoCursoCachimboEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (EstadoCursoCachimboEnum d : EstadoCursoCachimboEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
