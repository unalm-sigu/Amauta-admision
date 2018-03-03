package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;


public interface infoAcademicoService {
    
     ObjectNode allAlumnosByCiclo(Alumno alumno,Long numeroCiclo);
}
