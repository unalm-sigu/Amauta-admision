package pe.edu.lamolina.pivot.controller.programacionhorarios.tramiteaula;

import java.util.HashMap;
import java.util.Map;

public enum TipoSolicitanteEnum {

    ALUMNO("Alumno"),
    DOCENTE("Profesor"),
    INSTITUCION("Institución");

    private final String value;
    private static final Map<String, TipoSolicitanteEnum> lookup = new HashMap<>();

    static {
        for (TipoSolicitanteEnum d : TipoSolicitanteEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoSolicitanteEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoSolicitanteEnum get(String valor) {
        return lookup.get(valor);
    }

}
