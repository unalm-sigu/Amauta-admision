package pe.edu.lamolina.pivot.controller.academico.updatehistorialacademico;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface UpdateHistorialAcademicoService {

    Alumno allInfo(Alumno alumno);

    void updateHistorialAcademico(Alumno alumnoForm, DataSessionPivot ds);

    List<CicloAcademico> allCicloAcademico();

    ObjectNode toJson(Object object);

    List<AlumnoCiclo> allPromediosByAlumno(Alumno alumno);

}
