package pe.edu.lamolina.pivot.zelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class Laboratory {

    public static void main666(String[] args) {

        String conte = "__AULA__<p style=\"text-align:right\">__NUMERO__-DRAD/__SERIE__</p>\n"
                + "\n"
                + "<p style=\"text-align:right\">&nbsp;</p>\n"
                + "\n"
                + "<p style=\"text-align:right\">&nbsp;</p>\n"
                + "\n"
                + "<p style=\"text-align:right\">&nbsp;</p>\n"
                + "\n"
                + "<p style=\"text-align:right\">&nbsp;</p>\n"
                + "\n"
                + "<h1><strong>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; __NOMBRE__</strong></h1>\n"
                + "\n"
                + "<p style=\"text-align:justify\">Con matr&iacute;cula N&ordm; __CODIGOALUMNO__, es __ALUMNO__ de la Facultad de <strong>__FACULTAD__</strong>, ha realizado estudios en dicha Facultad desde el __CICLOINICIOROMANO_ ciclo de __YEARINICIOCICLO__al __CICLOFINROMANO_ de __YEARFINCICLO__, en forma ininterrumpida.</p>\n"
                + "\n"
                + "<p style=\"text-align:justify\">Se encuentra __MATRICULADO__ en el ciclo _CICLOACTUAL__.</p>\n"
                + "\n"
                + "<p style=\"text-align:justify\">&nbsp;</p>\n"
                + "\n"
                + "<p style=\"text-align:center\">__FECHA__</p>\n"
                + "\n"
                + "<p style=\"text-align:center\">&nbsp;</p>\n"
                + "\n"
                + "<p style=\"text-align:center\">&nbsp;</p>\n"
                + "\n"
                + "<p style=\"text-align:center\">__JEFEOFICINA__</p>\n"
                + "\n"
                + "<p style=\"text-align:center\">&nbsp;</p>";

        String partes[] = conte.split("__");
        Map<String, String> mapVariables = new LinkedHashMap();
        for (String parte : partes) {
            if (isAlpha(parte)) {
                String variable = "__" + parte + "__";
                if (conte.contains(variable)) {
                    mapVariables.put(variable, variable);
                }
            }
        }

        for (Map.Entry<String, String> entry : mapVariables.entrySet()) {
            System.out.println(entry.getKey());
        }

    }

    public static boolean isAlpha(String name) {
        return name.matches("[0-9A-Z]+");
    }
}
