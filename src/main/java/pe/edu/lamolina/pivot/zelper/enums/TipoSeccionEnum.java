package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoSeccionEnum {
    TEO("Teoría", TipoSeccionEvalEnum.TEO, TipoCursoEnum.TEO), //TEO
    PRA("Práctica", TipoSeccionEvalEnum.PRAC, TipoCursoEnum.TEOPRA), //PRA
    TCUR("Teoría Curso", TipoSeccionEvalEnum.TEO, TipoCursoEnum.TEOPRA), //TEOPRA
    PCUR("Práctica Curso", TipoSeccionEvalEnum.PRAC, TipoCursoEnum.TEOPRA); //TEOPRA,,, la primira vez no se hace el merge

    private final String value;
    private final TipoSeccionEvalEnum tipoSeccionEvalEnum;
    private final TipoCursoEnum tipoCursoEnum;
    private static final Map<String, TipoSeccionEnum> lookup = new HashMap<>();

    static {
        for (TipoSeccionEnum d : TipoSeccionEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoSeccionEnum(String value, TipoSeccionEvalEnum tipoSeccionEvalEnum, TipoCursoEnum tipoCursoEnum) {
        this.value = value;
        this.tipoSeccionEvalEnum = tipoSeccionEvalEnum;
        this.tipoCursoEnum = tipoCursoEnum;
    }

    public String getValue() {
        return value;
    }

    public TipoSeccionEvalEnum getTipoSeccionEvalEnum() {
        return tipoSeccionEvalEnum;
    }

    public TipoCursoEnum getTipoCursoEum() {
        return tipoCursoEnum;
    }

    public static TipoSeccionEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoSeccionEnum d : TipoSeccionEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }

}
