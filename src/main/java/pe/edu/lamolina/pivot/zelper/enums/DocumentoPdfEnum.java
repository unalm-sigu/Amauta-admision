package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum DocumentoPdfEnum {

    ACTA_NOTAS("ActaNotas", "pdf/actaDeNotas", "Acta de Notas", "Acta de Notas");

    private final String name;
    private final String fileTemplate;
    private final String title;
    private final String subject;

    private static final Map<String, DocumentoPdfEnum> lookup = new HashMap<>();

    static {
        for (DocumentoPdfEnum d : DocumentoPdfEnum.values()) {
            lookup.put(d.getName(), d);
        }
    }

    private DocumentoPdfEnum(String name, String fileTemplate, String title, String subject) {
        this.name = name;
        this.fileTemplate = fileTemplate;
        this.title = title;
        this.subject = subject;
    }

    public static DocumentoPdfEnum getEnum(String name) {
        for (DocumentoPdfEnum d : DocumentoPdfEnum.values()) {
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
