package pe.edu.lamolina.pivot.zelper;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import pe.albatross.zelpers.miscelanea.CodeGenerator;

public class Laboratory {

    public static void main666(String[] args) {

        List<String> codigos = new ArrayList();

        for (int i = 0; i < 2000; i++) {
            String codigo = StringUtils.leftPad(CodeGenerator.getNextCode(codigos, 0), 3, '0');
            System.out.println(codigo);
            codigos.add(codigo);
        }
    }

}
