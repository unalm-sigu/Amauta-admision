package pe.edu.lamolina.pivot.zelper;

import java.math.BigDecimal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;

public class Laboratory {

    public static void main666(String[] args) {

        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title></title>"
                + "</head>"
                + "<body><p>&nbsp;</p>"
                + ""
                + "<p><strong>CONSTANCIA N&ordm; @(CODIGO-CONSTANCIA)</strong></p>"
                + ""
                + "<p>&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;<strong>A QUIEN CONCIERNE</strong></p>"
                + ""
                + "<p>LA QUE SUSCRIBE, JEFA DEL DEPARTAMENTO DE REGISTRO DE LA UNIVERSIDAD NACIONAL AGRARIA, LA MOLINA:</p>"
                + ""
                + "<p><strong>CERTIFICA:</strong></p>"
                + ""
                + "<p>&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp;Que, &nbsp;la @(SENOR-A). &nbsp;<strong>@(APELLIDO-PERSONA)</strong>, identificada con el N&ordm; <strong>@(MATRICULA)&nbsp;</strong>en el Registro de Matr&iacute;cula, ha realizado estudios por el <strong> </strong>@(PROGRAMA) en esta Universidad durante&nbsp;@(CICLOS-CURSADOS) , habiendo aprobado los siguiente(s) curso(s):</p>"
                + ""
                + "<p>&nbsp;</p>"
                + ""
                + "<table align='center' border='0' cellpadding='1' cellspacing='1' style='width:750px'>"
                + "<thead>"
                + "<tr>"
                + "<th scope='col'>"
                + "<p style='text-align:center'><strong>CODIGO&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;</strong></p>"
                + "</th>"
                + "<th scope='col' style='text-align:center'><strong>NOMBRE DEL CURSO</strong></th>"
                + "<th scope='col' style='text-align:center'><strong>NOTA &nbsp; &nbsp;</strong></th>"
                + "<th scope='col'>"
                + "<p style='text-align:center'><strong>CREDITO</strong></p>"
                + "</th>"
                + "</tr>"
                + "</thead>"
                + "<tbody>"
                + "<tr class='albLoop'>"
                + "<td>"
                + "<p style='text-align:center'>@(TABLA-CODIGO-CURSO)</p>"
                + "</td>"
                + "<td style='text-align:center'>"
                + "<p>@(TABLA-CURSO)</p>"
                + "</td>"
                + "<td style='text-align:center'>"
                + "<p>@(TABLA-CURSO-NOTA)</p>"
                + "</td>"
                + "<td style='text-align:center'>"
                + "<p>@(TABLA-CURSO-CREDITO)</p>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + ""
                + "<p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<br />"
                + "&nbsp; &nbsp; &nbsp;&nbsp;&nbsp; &nbsp;Se expide la presente constancia, a solicitud de la interesada y para los fines que estime conveniente.&nbsp;</p>"
                + ""
                + "<p><br />"
                + "La Molina, @(FECHA-CONSTANCIA)</p>"
                + ""
                + "<p><br />"
                + "&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</p>"
                + ""
                + "<p style='text-align:center'>Lic. Victoria Arru&eacute; Mu&ntilde;oz<br />"
                + "&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp; &nbsp; &nbsp;JEFA &nbsp;DEPARTAMENTO DE REGISTRO<br />"
                + "&nbsp;</p>"
                + ""
                + "<p>&nbsp;</p>"
                + "</body>"
                + "</html>";
        Document html = Jsoup.parse(htmlContent);

//        for (int i = 0; i < 7; i++) {
//            html(htmlContent).attr("alb").toString();
//        }
        Element span = html.select("tr").get(1);
//        Elements tr = html.getElementsByClass("albLoop");
//        for (Element element : tr) {
        String var = "<td>hola como estas</td>";
        span.replaceWith(new Element("tr").append(var));
//        span.wrap("Hola");
//        }

        System.out.println(html.html());

    }

}
