package pe.edu.lamolina.pivot.zelper;

import java.util.Arrays;
import pe.edu.lamolina.model.enums.TipoCreditoEnum;

public class Laboratory {

    public static void main666(String[] args) {
        System.out.println(Arrays.stream(TipoCreditoEnum.values()).anyMatch((t) -> t.name().equals("DAILY1")));
    }

}
