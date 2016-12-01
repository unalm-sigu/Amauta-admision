package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum OrigenPlanCalificaEnum {
    DEP("Dpto. Academico"),
    DOC("Docente");

    private final String value;
    private static final Map<String, OrigenPlanCalificaEnum> lookup = new HashMap<>();

    static {
        for (OrigenPlanCalificaEnum d : OrigenPlanCalificaEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private OrigenPlanCalificaEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OrigenPlanCalificaEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (OrigenPlanCalificaEnum d : OrigenPlanCalificaEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }

        return nombre;
    }

    public static boolean existValue(String looked) {
        boolean exist = false;

        for (OrigenPlanCalificaEnum e : OrigenPlanCalificaEnum.values()) {
            if (e.name().equals(looked)) {
                exist = true;
                break;
            }
        }

        return exist;
    }
}
