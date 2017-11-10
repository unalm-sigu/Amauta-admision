package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum OficinaJefeAusenteEnum {

    ACT("Ausente"), ENF("Enfermo");

    private final String value;
    private static final Map<String, OficinaJefeAusenteEnum> lookup = new HashMap<>();

    static {
        for (OficinaJefeAusenteEnum d : OficinaJefeAusenteEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private OficinaJefeAusenteEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OficinaJefeAusenteEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (OficinaJefeAusenteEnum d : OficinaJefeAusenteEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
