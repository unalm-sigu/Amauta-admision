package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum EstadoAlumnoHorarioEnum {

    PEND("Pendiente"), 
    MATR("Matriculado");

    private final String value;
    private static final Map<String, EstadoAlumnoHorarioEnum> lookup = new HashMap<>();

    static {
        for (EstadoAlumnoHorarioEnum d : EstadoAlumnoHorarioEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private EstadoAlumnoHorarioEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoAlumnoHorarioEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (EstadoAlumnoHorarioEnum d : EstadoAlumnoHorarioEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
