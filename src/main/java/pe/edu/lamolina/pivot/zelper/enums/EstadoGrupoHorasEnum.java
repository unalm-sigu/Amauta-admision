package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum EstadoGrupoHorasEnum {

    COM("Completo"), INC("Incompleto"),;

    private final String value;
    private static final Map<String, EstadoGrupoHorasEnum> lookup = new HashMap<>();

    static {
        for (EstadoGrupoHorasEnum d : EstadoGrupoHorasEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private EstadoGrupoHorasEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoGrupoHorasEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (EstadoGrupoHorasEnum d : EstadoGrupoHorasEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
