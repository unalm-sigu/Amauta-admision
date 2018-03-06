package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ObjectNode;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface infoAcademicoService {

    ObjectNode allAlumnosByCiclo(Alumno alumno, Long numeroCiclo);

    ObjectNode  allAlumnosByCursosMatri(Alumno alumno, CicloAcademico cicloAca);

}
