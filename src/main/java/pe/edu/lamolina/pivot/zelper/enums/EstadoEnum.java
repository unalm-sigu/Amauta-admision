package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum EstadoEnum {

    ACT("Activo"), CER("Cerrado"), CRE("Creado"), INA("Inactivo"), ANU("Anulado"),;

    private final String value;
    private static final Map<String, EstadoEnum> lookup = new HashMap<>();

    static {
        for (EstadoEnum d : EstadoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private EstadoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (EstadoEnum d : EstadoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
