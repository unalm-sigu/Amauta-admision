package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoGrupoHorariosEnum {

    OBL("Obligatorio"),  FLX("Flexible"), FLXHOR("Flexible con Horas"),;

    private final String value;
    private static final Map<String, TipoGrupoHorariosEnum> lookup = new HashMap<>();

    static {
        for (TipoGrupoHorariosEnum d : TipoGrupoHorariosEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoGrupoHorariosEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoGrupoHorariosEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoGrupoHorariosEnum d : TipoGrupoHorariosEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
