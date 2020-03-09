package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TramiteCondicionalService {

    List<CicloAcademico> allCiclos(CicloAcademico academico);

    List<Tramite> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter);

    void saveRetiroCiclo(Tramite tramite, DataSessionPivot ds);

    String updateRetiroCiclo(Tramite tramite, DataSessionPivot ds);

    List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds);

    List<TipoTramite> allTipoTramite();

    void saveReincorporacion(Tramite tramite, DataSessionPivot ds);

    String updateReincorporacion(Tramite tramite, DataSessionPivot ds);

    void saveCambioNota(Tramite tramite, DataSessionPivot ds);

    String updateCambioNota(Tramite tramite, DataSessionPivot ds);

    List<Curso> allCursosByName(String nombre, Alumno alumno, CicloAcademico academico, DataSessionPivot ds);

    void evaluarEliminarMatriculable(Alumno alumno, CicloAcademico cicloAcademico, DataSessionPivot ds);
}
