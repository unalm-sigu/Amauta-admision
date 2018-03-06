package pe.edu.lamolina.pivot.controller.academico.situacionacademica;

import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.SituacionAcademica;

public interface SituacionAcademicaService {

    SituacionAcademica findSituacionFinal(AlumnoCiclo alumnoCiclo, SituacionAcademica situacionAcademicaIni, Integer ciclosEstudiados, Integer capa, CicloAcademico cicloAcademico);
}
