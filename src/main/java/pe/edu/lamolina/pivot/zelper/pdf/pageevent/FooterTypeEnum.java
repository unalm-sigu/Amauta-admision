package pe.edu.lamolina.pivot.zelper.pdf.pageevent;

import java.util.HashMap;
import java.util.Map;

public enum FooterTypeEnum {

    FOOTER1("Datos con linea horizontal"),
    FOOTER2("Datos sin linea horizontal"),
    FOOTER3("Enumaracion de Paginas solo con número");

    private final String value;
    private static final Map<String, FooterTypeEnum> lookup = new HashMap<>();

    static {
        for (FooterTypeEnum d : FooterTypeEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private FooterTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
