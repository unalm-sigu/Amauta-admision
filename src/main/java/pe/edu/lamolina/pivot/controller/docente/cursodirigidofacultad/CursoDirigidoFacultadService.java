package pe.edu.lamolina.pivot.controller.docente.cursodirigidofacultad;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.tramite.CursoDirigidoFacultad;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CursoDirigidoFacultadService {

    List<Facultad> findByDocente(DataSessionPivot ds);

    List<CursoDirigidoFacultad> allByDocenteFacultadDynatable(Facultad facultad, DynatableFilter filter);

    List<Curso> allCursoLikeParam(String parametro);

    void save(CursoDirigidoFacultad cursoDirigidoFacultad, DataSessionPivot ds);

}
