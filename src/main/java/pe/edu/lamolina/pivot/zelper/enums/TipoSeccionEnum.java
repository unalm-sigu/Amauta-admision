package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoSeccionEnum {
    PRA("Práctica", TipoSeccionEvalEnum.PRAC), PCUR("Práctica Curso", TipoSeccionEvalEnum.PRAC),
    TCUR("Teoría Curso", TipoSeccionEvalEnum.TEO), TEO("Teoría", TipoSeccionEvalEnum.TEO);

    private final String value;
    private final TipoSeccionEvalEnum tipoSeccionEvalEnum;
    private static final Map<String, TipoSeccionEnum> lookup = new HashMap<>();

    static {
        for (TipoSeccionEnum d : TipoSeccionEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoSeccionEnum(String value, TipoSeccionEvalEnum tipoSeccionEvalEnum) {
        this.value = value;
        this.tipoSeccionEvalEnum = tipoSeccionEvalEnum;
    }

    public String getValue() {
        return value;
    }

    public TipoSeccionEvalEnum getTipoSeccionEvalEnum() {
        return tipoSeccionEvalEnum;
    }

    public static TipoSeccionEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoSeccionEnum d : TipoSeccionEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }

}
