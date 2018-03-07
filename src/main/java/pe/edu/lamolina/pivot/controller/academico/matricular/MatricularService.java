package pe.edu.lamolina.pivot.controller.academico.matricular;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.TurnoAtencion;

public interface MatricularService {

    TurnoAtencion findTurnoAtencion(Long turno);

    void matricular(TurnoAtencion turnoAtencion, CicloAcademico cicloAcademico);

    Long countAllAlumnoPrematriculado(CicloAcademico cicloAcademico);

    Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico);

}
