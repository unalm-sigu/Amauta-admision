package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum LetraGrupoHoraEnum {

    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    F("F"),
    G("G"),
    H("H"),
    I("I"),
    J("J"),
    K("K"),
    L("L"),
    M("M"),
    N("N"),
    O("O"),
    P("P"),
    Q("Q"),
    R("R"),
    S("S"),
    T("T"),
    U("U"),
    V("V"),
    W("W");

    private final String value;
    private static final Map<String, LetraGrupoHoraEnum> lookup = new HashMap<>();

    static {
        for (LetraGrupoHoraEnum d : LetraGrupoHoraEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private LetraGrupoHoraEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LetraGrupoHoraEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (LetraGrupoHoraEnum d : LetraGrupoHoraEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }

        return nombre;
    }

    public static boolean existValue(String looked) {
        boolean exist = false;

        for (LetraGrupoHoraEnum e : LetraGrupoHoraEnum.values()) {
            if (e.name().equals(looked)) {
                exist = true;
                break;
            }
        }

        return exist;
    }
}
