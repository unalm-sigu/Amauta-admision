package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum EstadoMatriculaCursoEnum {
    MAT("Mariculado"),
    RET("Retirado"),
    RCU("Ret. Curso"),
    RCI("Ret. Ciclo");

    private final String value;
    private static final Map<String, EstadoMatriculaCursoEnum> lookup = new HashMap<>();

    static {
        for (EstadoMatriculaCursoEnum d : EstadoMatriculaCursoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private EstadoMatriculaCursoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoMatriculaCursoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

}
