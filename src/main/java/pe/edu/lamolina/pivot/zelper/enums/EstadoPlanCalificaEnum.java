package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum EstadoPlanCalificaEnum {
    CRE("Creado"),
    ACT("Activo"),
    INA("Inactivo"),
    //APR("Aprobado"),
    //DES("Desaprobado"),
    OBS("Observado"),
    SOL("Solicitado"),
    RHZ("Rechazado"),
    REE("Reenviado"),
    CER("Cerrado"),
    PRO("Propuesto"),
    EXPR("Expandir"),
    EXP("Epandido"),
    ACEP("Aceptado");

    private final String value;
    private static final Map<String, EstadoPlanCalificaEnum> lookup = new HashMap<>();

    static {
        for (EstadoPlanCalificaEnum d : EstadoPlanCalificaEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private EstadoPlanCalificaEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EstadoPlanCalificaEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (EstadoPlanCalificaEnum d : EstadoPlanCalificaEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }

        return nombre;
    }

    public static boolean existValue(String looked) {
        boolean exist = false;

        for (EstadoPlanCalificaEnum e : EstadoPlanCalificaEnum.values()) {
            if (e.name().equals(looked)) {
                exist = true;
                break;
            }
        }

        return exist;
    }
}
