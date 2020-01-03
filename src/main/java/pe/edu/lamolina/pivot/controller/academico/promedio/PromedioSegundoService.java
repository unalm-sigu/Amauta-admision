package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PromedioSegundoService {

    void procesarYear(
            List<Alumno> alumnos,
            CicloAcademico cicloActivo,
            List<CicloAcademico> ciclos,
            DataSessionPivot ds);

}
