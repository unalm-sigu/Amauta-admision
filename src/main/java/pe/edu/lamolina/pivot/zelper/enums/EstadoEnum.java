package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum EstadoEnum {

    ACT("Activo", "success"), CER("Cerrado", "danger"), CRE("Creado", "default"), INA("Inactivo", "danger"), ANU("Anulado", "danger");

    private final String value;
    private final String classCss;
    private static final Map<String, EstadoEnum> lookup = new HashMap<>();

    static {
        for (EstadoEnum d : EstadoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private EstadoEnum(String value, String classCss) {
        this.value = value;
        this.classCss = classCss;
    }

    public String getValue() {
        return value;
    }

    public String getClassCss() {
        return classCss;
    }

    public static EstadoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (EstadoEnum d : EstadoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
