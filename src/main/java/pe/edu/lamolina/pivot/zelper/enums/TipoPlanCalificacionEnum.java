package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoPlanCalificacionEnum {
    PLANT("Plantilla"),
    GPO_SEC("Grupo Seccion");

    private final String value;
    private static final Map<String, TipoPlanCalificacionEnum> lookup = new HashMap<>();

    static {
        for (TipoPlanCalificacionEnum d : TipoPlanCalificacionEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoPlanCalificacionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoPlanCalificacionEnum get(String abbreviation) {
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
