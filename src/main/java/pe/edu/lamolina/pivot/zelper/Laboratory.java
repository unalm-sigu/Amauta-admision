package pe.edu.lamolina.pivot.zelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Laboratory {

    public static void main666(String[] args) {

        BigDecimal capa = new BigDecimal(121);
        BigDecimal cca = new BigDecimal(209);
        BigDecimal caps = new BigDecimal(0);
        BigDecimal ccs = new BigDecimal(9);
        BigDecimal pps = new BigDecimal(0);

        caps = caps.equals(BigDecimal.ZERO) ? new BigDecimal(0.004) : caps;
        capa = capa.equals(BigDecimal.ZERO) ? new BigDecimal(0.004) : capa;
        pps = pps.equals(BigDecimal.ZERO) ? new BigDecimal(0.004) : pps;

        BigDecimal factor1 = capa.multiply(caps).multiply(pps);
        BigDecimal factor2 = ccs.multiply(cca);
        factor2 = factor2.equals(BigDecimal.ZERO) ? BigDecimal.ONE : factor2;

        BigDecimal puntajePrioridad = factor1.divide(factor2, 6, RoundingMode.FLOOR);

        System.out.println(puntajePrioridad);

    }

}
