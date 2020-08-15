package pe.edu.lamolina.amauta.zelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Laboratory {

    public static void main(String[] args) {

         BigDecimal perAvance = new BigDecimal(500).divide(new BigDecimal(6783),2, RoundingMode.HALF_UP);
         System.out.println("pe.edu.lamolina.amauta.zelper.Laboratory.main666()" + perAvance);

    }

}
