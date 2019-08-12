package pe.edu.lamolina.pivot.zelper;

import java.math.BigDecimal;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import static pe.edu.lamolina.model.enums.AportesEnum.A05;

public class Laboratory {

    public static void main(String[] args) {
        ResumenAporteAlumno aportante = new ResumenAporteAlumno();
        aportante.setMontoTotal(BigDecimal.ONE);
        if (aportante.getMontoTotal().compareTo(BigDecimal.ZERO) >= 0) {
            System.out.println("Fui");
            return;
        }
        String a = A05.name().substring(1);
        System.out.println(a);
    }

}
