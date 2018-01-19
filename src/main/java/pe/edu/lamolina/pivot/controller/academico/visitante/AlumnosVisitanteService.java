package pe.edu.lamolina.pivot.controller.academico.visitante;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoVisitante;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AlumnosVisitanteService {

    List<TipoDocIdentidad> allTiposDocIdentidad();

    void save(AlumnoVisitante alumnoVisitante, DataSessionPivot ds);

    List<CicloAcademico> allCicloAcademico();

    List<AlumnoVisitante> allAlumnoVisitante(DynatableFilter filter);

    Map<Long, Alumno> allAlumnoByVisitante(List<AlumnoVisitante> visitantes);

    void delete(AlumnoVisitante alumnoVisitante);

    AlumnoVisitante findAlumnoVisitante(Long idAlumnoVisitante);

    public void update(AlumnoVisitante alumnoVisitante, DataSessionPivot ds);

}
