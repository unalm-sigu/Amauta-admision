package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum EstadoTipoGrupoHorasEnum {

    ACT("Activo"),  CRE("Creado"), DES("Desactivado");

    private final String value;
    private static final Map<String, EstadoTipoGrupoHorasEnum> lookup = new HashMap<>();

    static {
        for (EstadoTipoGrupoHorasEnum d : EstadoTipoGrupoHorasEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private EstadoTipoGrupoHorasEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoTipoGrupoHorasEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (EstadoTipoGrupoHorasEnum d : EstadoTipoGrupoHorasEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
