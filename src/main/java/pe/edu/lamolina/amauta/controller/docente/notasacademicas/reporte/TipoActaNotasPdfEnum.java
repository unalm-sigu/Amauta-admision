package pe.edu.lamolina.amauta.controller.docente.notasacademicas.reporte;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public enum TipoActaNotasPdfEnum {

    ACTA_NOTAS("ActaNotas", "pdf/actaDeNotas", "Acta de Notas", "Acta de Notas");

    private final String name;
    private final String fileTemplate;
    private final String title;
    private final String subject;

    private static final Map<String, TipoActaNotasPdfEnum> lookup = new HashMap<>();

    static {
        for (TipoActaNotasPdfEnum d : TipoActaNotasPdfEnum.values()) {
            lookup.put(d.getName(), d);
        }
    }

    private TipoActaNotasPdfEnum(String name, String fileTemplate, String title, String subject) {
        this.name = name;
        this.fileTemplate = fileTemplate;
        this.title = title;
        this.subject = subject;
    }

    public static TipoActaNotasPdfEnum getEnum(String name) {
        for (TipoActaNotasPdfEnum d : TipoActaNotasPdfEnum.values()) {
            if (d.name().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getFileTemplate() {
        return fileTemplate;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

}
