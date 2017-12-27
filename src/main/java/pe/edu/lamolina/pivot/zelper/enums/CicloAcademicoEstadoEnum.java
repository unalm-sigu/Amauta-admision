package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum CicloAcademicoEstadoEnum {

    ACT("Activo"), CER("Cerrado"), CRE("Creado"), DES("Desactivado"), PEND("Pendiente"), CFG("Configurando");

    private final String value;
    private static final Map<String, CicloAcademicoEstadoEnum> lookup = new HashMap<>();

    static {
        for (CicloAcademicoEstadoEnum d : CicloAcademicoEstadoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private CicloAcademicoEstadoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CicloAcademicoEstadoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (CicloAcademicoEstadoEnum d : CicloAcademicoEstadoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }

        return nombre;
    }

    public static boolean existValue(String looked) {
        boolean exist = false;

        for (CicloAcademicoEstadoEnum e : CicloAcademicoEstadoEnum.values()) {
            if (e.name().equals(looked)) {
                exist = true;
                break;
            }
        }

        return exist;
    }
}
