package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum ModalidadEstudioEnum {

    PRE("Pre-Grado"),
    EPG("Postgrado"),
    ESP("Especial"),
    VIS("Visitante");

    private final String value;
    private static final Map<String, ModalidadEstudioEnum> lookup = new HashMap<>();

    static {
        for (ModalidadEstudioEnum d : ModalidadEstudioEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private ModalidadEstudioEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ModalidadEstudioEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (ModalidadEstudioEnum d : ModalidadEstudioEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }

        return nombre;
    }

    public static boolean existValue(String looked) {
        boolean exist = false;

        for (ModalidadEstudioEnum e : ModalidadEstudioEnum.values()) {
            if (e.name().equals(looked)) {
                exist = true;
                break;
            }
        }

        return exist;
    }
}
