package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

public interface EvaluacionDAO extends Crud<Evaluacion> {

    List<Evaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion);

    List<Evaluacion> allBySecciones(List<Seccion> secciones);

    Long countEvaluacionesFaltantesByGrupo(Long idGrupoSeccion);

    List<Evaluacion> allByEvaluacionSeccion(EvaluacionSeccion evaluacionSeccion);

}
