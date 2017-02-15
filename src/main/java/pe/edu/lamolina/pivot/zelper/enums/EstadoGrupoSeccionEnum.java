package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum EstadoGrupoSeccionEnum {

    ABI("Abierto"),
    CER("Cerrado"),
    RAB("Reabierto");

    private final String value;
    private static final Map<String, EstadoGrupoSeccionEnum> lookup = new HashMap<>();

    static {
        for (EstadoGrupoSeccionEnum d : EstadoGrupoSeccionEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private EstadoGrupoSeccionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoGrupoSeccionEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

}
