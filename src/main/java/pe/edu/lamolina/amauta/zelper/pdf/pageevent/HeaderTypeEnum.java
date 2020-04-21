package pe.edu.lamolina.amauta.zelper.pdf.pageevent;

import java.util.HashMap;
import java.util.Map;

public enum HeaderTypeEnum {

    HEADER1("Logo, titulo y 2 subtitulos", 70),
    HEADER2("UNO O DOS TITULOS", 23);

    private final String value;
    private final float relativeMarginTop;
    private static final Map<String, HeaderTypeEnum> lookup = new HashMap<>();

    static {
        for (HeaderTypeEnum d : HeaderTypeEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private HeaderTypeEnum(String value, float relativeMarginTop) {
        this.value = value;
        this.relativeMarginTop = relativeMarginTop;
    }

    public String getValue() {
        return value;
    }

    public float getRelativeMarginTop() {
        return relativeMarginTop;
    }

}
