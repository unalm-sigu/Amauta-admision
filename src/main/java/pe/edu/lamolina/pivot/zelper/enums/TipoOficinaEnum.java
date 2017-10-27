package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoOficinaEnum {

    OFI("Oficina"),
    FAC("Facultad"),
    DEP("Departamento"),
    ESP("Especialidad"),
    DPTO("Indefinido"),
    EPG("Indefinido"),
    RECT("Indefinido"),
    UNA("Indefinido"),
    AREA("Indefinido"),
    VICE("Area");

    private final String value;
    private static final Map<String, TipoOficinaEnum> lookup = new HashMap<>();

    static {
        for (TipoOficinaEnum d : TipoOficinaEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoOficinaEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoOficinaEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoOficinaEnum d : TipoOficinaEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
