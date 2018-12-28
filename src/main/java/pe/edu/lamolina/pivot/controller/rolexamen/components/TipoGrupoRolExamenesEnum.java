package pe.edu.lamolina.pivot.controller.rolexamen.components;

import java.util.HashMap;
import java.util.Map;

public enum TipoGrupoRolExamenesEnum {

    CUR_MAS("Curso Masivo"),
    GRU_REG("Grupo Regular"),
    GRU_ESP("Grupo Especial");

    private final String value;
    private static final Map<String, TipoGrupoRolExamenesEnum> lookup = new HashMap<>();

    static {
        for (TipoGrupoRolExamenesEnum d : TipoGrupoRolExamenesEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoGrupoRolExamenesEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getCode() {
        return this.name();
    }

    public static TipoGrupoRolExamenesEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

}
