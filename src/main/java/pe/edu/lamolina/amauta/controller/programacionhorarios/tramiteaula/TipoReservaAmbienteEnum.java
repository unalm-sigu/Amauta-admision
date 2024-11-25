package pe.edu.lamolina.amauta.controller.programacionhorarios.tramiteaula;

import java.util.HashMap;
import java.util.Map;

public enum TipoReservaAmbienteEnum {
    EPG("Posgrado"),
    PRE("Pregrado"),
    LIB("Libre");

    private final String value;
    private static final Map<String, TipoReservaAmbienteEnum> lookup = new HashMap<>();

    static {
        for(TipoReservaAmbienteEnum d : TipoReservaAmbienteEnum.values()) {
            lookup.put(d.value, d);
        }
    }

    private TipoReservaAmbienteEnum(String value) {this.value = value;}
    public String getId() {
        return this.name();
    }
    public String getValue() {return value;}
    public static TipoReservaAmbienteEnum get(String abbreviation){return lookup.get(abbreviation);}

    public static String getNombre(String nombre){
        for(TipoReservaAmbienteEnum d : TipoReservaAmbienteEnum.values()) {
            if(d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();}
        }
        return nombre;
    }

}
