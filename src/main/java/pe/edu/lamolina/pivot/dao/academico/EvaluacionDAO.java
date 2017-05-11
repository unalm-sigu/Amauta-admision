package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

public interface EvaluacionDAO extends Crud<Evaluacion> {

    List<Evaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idEvaluacionExpandida);

    List<Evaluacion> allBySecciones(List<Seccion> secciones);

    Long countEvaluacionesFaltantesByGrupo(Long idGrupoSeccion);

    List<Evaluacion> allBySeccion(Seccion seccion);

    void deleteByEvaluacionExpandida(Long idEvaluacionExpandida);

    Evaluacion findByEvalExpSeccion(Long evaluacionExpansion, Long seccion);

    void updateDocenteEvaluador(Evaluacion evaluacion, Docente docente);

    List<Evaluacion> allByEvaluacionSeccion(EvaluacionSeccion evalSecc);

    List<Evaluacion> allByEvaluacionesExpandidas(List<EvaluacionExpandida> evaluacionesExp);

    List<Evaluacion> allByEvaluacionExpandidaSecciones(EvaluacionExpandida evaluacion, List<Seccion> secciones);

    void deleteEvaluacionesByEvaluacionSeccion(EvaluacionSeccion evaluacionSeccion);
}
