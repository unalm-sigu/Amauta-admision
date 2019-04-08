package pe.edu.lamolina.pivot.controller.rolexamen.util;

import java.util.HashMap;
import java.util.Map;

public enum TipoRolExamenesLoggerEnum {

    CUR_MAS("Cursos Masivos"), GPO_REG("Grupos Regulares"), GPO_ESP("Grupos Especiales"), //PARENTS
    TRAS_TO_CUR_MAS("Traslado a Cursos Masivos"), TRAS_TO_GPO_REG("Traslado a Grupo Regular"), //Traslados
    ACT_CUR_MAS("Activar Curso Masivo"), ACT_GPO_REG("Activar Grupo Regular"),
    CRU_DOC("Cruce Docente"), CRU_ALU("Cruce Alumno"), CRU_AUL("Cruce Aula"),
    AUL_OCUP("Aula Ocupada"); //HIJOS

    private final String value;
    private static final Map<String, TipoRolExamenesLoggerEnum> lookup = new HashMap<>();

    static {
        for (TipoRolExamenesLoggerEnum d : TipoRolExamenesLoggerEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoRolExamenesLoggerEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoRolExamenesLoggerEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoRolExamenesLoggerEnum d : TipoRolExamenesLoggerEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
