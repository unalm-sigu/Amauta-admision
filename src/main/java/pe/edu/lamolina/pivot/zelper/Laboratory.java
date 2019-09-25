package pe.edu.lamolina.pivot.zelper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import static pe.edu.lamolina.model.enums.AportesEnum.A05;

public class Laboratory {

    public static void main666(String[] args) {
        ResumenAporteAlumno aportante = new ResumenAporteAlumno();
        aportante.setMontoTotal(BigDecimal.ONE);
        String a = A05.name().substring(1);
        System.out.println(a);

        Date fecha = new Date(java.sql.Date.valueOf(LocalDate.of(2019, 4, 20)).getTime());
        System.out.println(fecha);
    }

}
