package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum TipoSeccionGrupoEnum {
    PRA("Práctica", TipoSeccionEvalEnum.PRAC, TipoCursoEnum.TEOPRA), //PRA
    TEO("Teoría", TipoSeccionEvalEnum.TEO, TipoCursoEnum.TEO); //TEO

    private final String value;
    private final TipoSeccionEvalEnum tipoSeccionEvalEnum;
    private final TipoCursoEnum tipoCursoEnum;
    private static final Map<String, TipoSeccionGrupoEnum> lookup = new HashMap<>();

    static {
        for (TipoSeccionGrupoEnum d : TipoSeccionGrupoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private TipoSeccionGrupoEnum(String value, TipoSeccionEvalEnum tipoSeccionEvalEnum, TipoCursoEnum tipoCursoEnum) {
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

    public static TipoSeccionGrupoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (TipoSeccionGrupoEnum d : TipoSeccionGrupoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }
        return nombre;
    }

}
