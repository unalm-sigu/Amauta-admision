package pe.edu.lamolina.pivot.zelper;

import java.util.List;
import pe.albatross.zelpers.miscelanea.CodeGenerator;

public class Laboratory {

    public static void main666(String[] args) {
        
        List<String> codigos = CodeGenerator.getCodes(1190, 20);
            
        for (String codigo : codigos) {
            System.out.println(codigo);
        }
    }

}
