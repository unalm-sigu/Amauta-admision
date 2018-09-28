package pe.edu.lamolina.pivot.zelper;

public class Laboratory {

    private static final String fmt = "  %11s:  %s %s%n";

    public static void main666(String[] args) {
        String code = "<p>@(ESTIMADO)&nbsp;@(NOMBRE-PERSONA):</p>nn<p>Sea bienvenido a la Universidad Agraria La Molina. Le informamos que hemos creado una cuenta de correo electr&oacute;nico para comunicarnos oficialmente con usted. La cuenta es:</p>nn<p>@(CORREO-NUEVO)</p>nn<p>&nbsp;</p>nn<p>Saludos<br />nUniversidad Nacional Agraria La Molina</p>";

        System.out.println(code.indexOf("@(ESTIMADO)")+" :::: ");
        System.out.println(code.replace("@(ESTIMADO)", "EsTiMaDo")+" :::: ");

    }

}
