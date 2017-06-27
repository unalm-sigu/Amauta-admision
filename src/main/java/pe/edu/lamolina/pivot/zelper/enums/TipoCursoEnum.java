package pe.edu.lamolina.pivot.zelper.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TipoCursoEnum {

    TEO("Teoría", TipoSeccionEvalEnum.TEO),
    PRA("Práctica", TipoSeccionEvalEnum.PRAC),
    TEOPRA("Teoría Práctica", TipoSeccionEvalEnum.TEO);

    private final String value;
    private static final Map<String, TipoCursoEnum> lookup = new HashMap<>();
    public static List<TipoCursoEnum> list = new ArrayList<TipoCursoEnum>();
    private final TipoSeccionEvalEnum tipoSeccionEvalEnum;

    static {
        for (TipoCursoEnum d : TipoCursoEnum.values()) {
            lookup.put(d.getValue(), d);
            list.add(d);
        }
    }

    private TipoCursoEnum(String value, TipoSeccionEvalEnum tipoSeccionEvalEnum) {
        this.tipoSeccionEvalEnum = tipoSeccionEvalEnum;
        this.value = value;

    }

    public String getValue() {
        return value;
    }

    public TipoSeccionEvalEnum getTipoSeccionEvalEnum() {
        return tipoSeccionEvalEnum;
    }

    public static TipoCursoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoCursoEnum d : TipoCursoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }
}
