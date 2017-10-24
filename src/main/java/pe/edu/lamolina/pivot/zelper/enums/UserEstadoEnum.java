package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserEstadoEnum {

    ACT("Activo"),
    INA("Inactivo");

    private final String value;
    private static final Map<String, UserEstadoEnum> lookup = new HashMap<>();

    static {
        for (UserEstadoEnum d : UserEstadoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private UserEstadoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserEstadoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (UserEstadoEnum d : UserEstadoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }

        return nombre;
    }

    public static boolean existValue(String looked) {
        boolean exist = false;

        for (UserEstadoEnum e : UserEstadoEnum.values()) {
            if (e.name().equals(looked)) {
                exist = true;
                break;
            }
        }

        return exist;
    }
}
