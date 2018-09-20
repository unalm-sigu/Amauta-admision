package pe.edu.lamolina.pivot.zelper;

import org.apache.commons.lang3.StringUtils;

public class Laboratory {

    private static final String fmt = "  %11s:  %s %s%n";

    public static void main666(String[] args) {
        String code = "";
        if (!StringUtils.isEmpty(code)) {
            String[] sodes = code.split("/");
            System.out.println(sodes.length);
            for (String sode : sodes) {
                System.out.println(sode);
            }
        }

    }

}
