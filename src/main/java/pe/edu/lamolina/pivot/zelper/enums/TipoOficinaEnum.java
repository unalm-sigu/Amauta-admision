package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Facultad;

public enum TipoOficinaEnum {

    OFI("Oficina", "null"),
    UNA("Unidad", "null"),
    RECT("Rectorado", "null"),
    VICE("Vicerrectorado", "null"),
    AREA("Area", "null"),
    EPG("Escuela Posgrado", "null"),
    FAC("Facultad", Facultad.class.getName()),
    DPTO("Departamento", DepartamentoAcademico.class.getName()),
    ESP("Especialidad", Carrera.class.getName());

    private final String value;
    private final String clase;
    private static final Map<String, TipoOficinaEnum> lookup = new HashMap<>();

    static {
        for (TipoOficinaEnum d : TipoOficinaEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoOficinaEnum(String value, String clase) {
        this.value = value;
        this.clase = clase;
    }

    public String getValue() {
        return value;
    }

    public String getClase() {
        return clase;
    }

    public static TipoOficinaEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoOficinaEnum d : TipoOficinaEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
