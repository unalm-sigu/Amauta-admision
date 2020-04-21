package pe.edu.lamolina.amauta.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoRestriccionEnum {

    ESP("Especialidad"),
    FAC("Facultad"),
    MOD("Modalidad"),
    NREP("No repitentes");

    private final String value;
    private static final Map<String, TipoRestriccionEnum> lookup = new HashMap<>();

    private TipoRestriccionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
