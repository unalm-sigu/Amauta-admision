package pe.edu.lamolina.pivot.zelper.enums;

import java.util.HashMap;
import java.util.Map;

public enum MenuTipoEnum {

    TITULO("Título"), MENU("Menú"),
    MENU_PADRE("Menú-Padre"), SUB_MENU("Sub-Menú"),
    OPCION("Opción"), BOTON("Botón");

    private final String value;
    private static final Map<String, MenuTipoEnum> lookup = new HashMap<>();

    static {
        for (MenuTipoEnum d : MenuTipoEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    private MenuTipoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MenuTipoEnum get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static String getNombre(String nombre) {

        for (MenuTipoEnum d : MenuTipoEnum.values()) {
            if (d.name().equalsIgnoreCase(nombre)) {
                return d.getValue();
            }
        }

        return nombre;
    }
}
