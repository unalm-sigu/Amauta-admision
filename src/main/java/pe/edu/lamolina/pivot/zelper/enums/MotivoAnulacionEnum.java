package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum MotivoAnulacionEnum {

    NOTA_MIN("Es la nota mínima"), NOTA_NCV("Nota convalidada");

    private final String value;
    private static final Map<String, MotivoAnulacionEnum> lookup = new HashMap<>();

    static {
        for (MotivoAnulacionEnum d : MotivoAnulacionEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private MotivoAnulacionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MotivoAnulacionEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (MotivoAnulacionEnum d : MotivoAnulacionEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
