package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoRestriccionEnum {

    ESP("Especialidad"),
    FAC("Facultad"),
    MOD("Modalidad");

    private final String value;
    private static final Map<String, TipoRestriccionEnum> lookup = new HashMap<>();

    private TipoRestriccionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
