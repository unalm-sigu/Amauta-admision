package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum RolEnum {

    TODO("Todo"),
    FAC("Facultad"),
    MOD("Modalidad"),
    ESP("Especialidad"),
    IOREA("Informático de Oficina de Estudios"),
    ALU("Alumno"),
    DOC("Docente"),
    OREA("Estudios Académicos"),
    RACD("Registro Académico"),
    PHOR("Programación de Horarios"),
    DPTO("Departamento Académico"),
    OPER_ADM("Operador Admisión"),
    CONF_ADM("Configurador Admisión"),
    PAGO_ADM("Pagos Admisión"),
    EXAM_ADM("Configurador Examen Admisión"),
    SEG_ADM("Seguridad Admisión");

    private final String value;
    private static final Map<String, RolEnum> lookup = new HashMap<>();

    static {
        for (RolEnum d : RolEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private RolEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RolEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (RolEnum d : RolEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
