package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoAmbienteEnum {
    AMB("Ambiente"), EDI("Edificio"), ESP("Espacio");

    private final String value;
    private static final Map<String, TipoAmbienteEnum> lookup = new HashMap<>();

    static {
        for (TipoAmbienteEnum d : TipoAmbienteEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoAmbienteEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoAmbienteEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoAmbienteEnum d : TipoAmbienteEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }

}
