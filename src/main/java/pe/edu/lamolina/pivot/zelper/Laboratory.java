package pe.edu.lamolina.pivot.zelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;

public class Laboratory {
    
    public static void main666(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        Alumno alu = new Alumno(45234);
        alu.setCicloIngreso(new CicloAcademico(33));
        alu.setMatriculaResumen(new ArrayList());
        alu.getMatriculaResumen().add(new MatriculaResumen(10));
        alu.getMatriculaResumen().add(new MatriculaResumen(12));
        
        try {
            String jsonInString = mapper.writeValueAsString(alu);
            System.out.println(jsonInString);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }
}
