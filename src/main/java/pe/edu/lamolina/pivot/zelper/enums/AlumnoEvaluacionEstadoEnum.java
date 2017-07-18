package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum AlumnoEvaluacionEstadoEnum {

    ACT("Activo", "Activo"),
    ANM("Anulada", "Anulada por nota mínima"),
    ANC("Convalidada", "Anulada por convalidación de nota"),
    CALC("Calculada", "Calculada por el sistema");

    private final String value;
    private final String detalle;
    private static final Map<String, AlumnoEvaluacionEstadoEnum> lookup = new HashMap<>();

    static {
        for (AlumnoEvaluacionEstadoEnum d : AlumnoEvaluacionEstadoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private AlumnoEvaluacionEstadoEnum(String value, String detalle) {
        this.value = value;
        this.detalle = detalle;
    }

    public String getValue() {
        return value;
    }

    public String getDetalle() {
        return detalle;
    }

    public static AlumnoEvaluacionEstadoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (AlumnoEvaluacionEstadoEnum d : AlumnoEvaluacionEstadoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
