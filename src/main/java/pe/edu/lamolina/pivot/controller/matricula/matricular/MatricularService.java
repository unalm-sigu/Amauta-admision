package pe.edu.lamolina.pivot.controller.matricula.matricular;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface MatricularService {

    TurnoAtencion findTurnoAtencion(Long turno);

    Long countAllAlumnoPrematriculado(CicloAcademico cicloAcademico);

    Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico);

    void matricular(TurnoAtencion turnoAtencion, DataSessionPivot ds);

    public CicloAcademico blequeoMatricula(CicloAcademico cicloAcademico);

    public CicloAcademico findCiclo(CicloAcademico cicloAcademico);

}
