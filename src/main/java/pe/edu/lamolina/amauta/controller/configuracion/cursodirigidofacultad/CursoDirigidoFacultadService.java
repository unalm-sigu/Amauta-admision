package pe.edu.lamolina.amauta.controller.configuracion.cursodirigidofacultad;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.tramite.CursoDirigidoFacultad;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface CursoDirigidoFacultadService {

    void save(CursoDirigidoFacultad cursoDirigidoFacultad, DataSessionPivot ds);

    List<CursoDirigidoFacultad> allByDynatable(DynatableFilter filter);

    List<Curso> allCursoLikeParamByFacultad(String parametro, Facultad facultad);

    void eliminar(CursoDirigidoFacultad cursoDirigidoFacultad);

}
