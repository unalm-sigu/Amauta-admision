package pe.edu.lamolina.pivot.zelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Laboratory {

    public static void main666(String[] args) {
        BigDecimal pps = new BigDecimal(6.818182);
        BigDecimal caps = new BigDecimal(0);
        BigDecimal capa = new BigDecimal(44);
        BigDecimal cca = new BigDecimal(72);
        BigDecimal ccs = new BigDecimal(11);

        caps = caps.equals(BigDecimal.ZERO) ? new BigDecimal(0.004) : caps;
        capa = capa.equals(BigDecimal.ZERO) ? new BigDecimal(0.004) : capa;
        pps = pps.equals(BigDecimal.ZERO) ? new BigDecimal(0.004) : pps;

        System.out.println("caps :" + caps);
        System.out.println("capa :" + capa);
        System.out.println("pps :" + pps);
        BigDecimal factor1 = capa.multiply(caps).multiply(pps);// capa.divide(cca, 12, RoundingMode.HALF_UP);
        BigDecimal factor2 = ccs.multiply(cca);//caps.divide(ccs, 12, RoundingMode.HALF_UP);
        factor2 = factor2 == BigDecimal.ZERO ? BigDecimal.ONE : factor2;
        System.out.println("Fac1 :" + factor1);
        System.out.println("Fac2 :" + factor2);
        BigDecimal puntajePrioridad = factor1.divide(factor2, 6, RoundingMode.HALF_UP);
        System.out.println(puntajePrioridad);
    }

}
