package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoGrupoHorasEnum {

    REGULAR("REGULAR"),  ESPECIAL("ESPECIAL"), ZETA("ZETA"),;

    private final String value;
    private static final Map<String, TipoGrupoHorasEnum> lookup = new HashMap<>();

    static {
        for (TipoGrupoHorasEnum d : TipoGrupoHorasEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoGrupoHorasEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoGrupoHorasEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoGrupoHorasEnum d : TipoGrupoHorasEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
