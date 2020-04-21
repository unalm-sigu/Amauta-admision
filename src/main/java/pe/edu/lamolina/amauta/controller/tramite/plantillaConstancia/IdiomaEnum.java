package pe.edu.lamolina.amauta.controller.tramite.plantillaConstancia;

import java.util.HashMap;
import java.util.Map;

public enum IdiomaEnum {

    DE("Alemán", "de"),
    JA("Japonés", "ja"),
    EN("Inglés", "en"),
    FR("Francés", "fr"),
    PT("Portugués", "pt"),
    ZH("Chino", "zh"),
    ES("Español", "es"),
    QU("Quechua", "qu"),
    AY("Aymara", "ay");

    private final String value;
    private final String acronimo;
    private static final Map<String, IdiomaEnum> lookup = new HashMap<>();

    static {
        for (IdiomaEnum d : IdiomaEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private IdiomaEnum(String value, String acronimo) {
        this.value = value;
        this.acronimo = acronimo;
    }

    public String getValue() {
        return value;
    }

    public String getAcronimo() {
        return acronimo;
    }

    public static IdiomaEnum get(String valor) {
        return lookup.get(valor);
    }

}
