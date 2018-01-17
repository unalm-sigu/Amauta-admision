package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;

public interface EvaluacionDAO extends EasyDAO<Evaluacion> {

    List<Evaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idEvaluacionExpandida);

    List<Evaluacion> allBySecciones(List<Seccion> secciones);

    Long countEvaluacionesFaltantesByGrupo(Long idGrupoSeccion);

    List<Evaluacion> allBySeccion(Seccion seccion);

    void deleteByEvaluacionExpandida(Long idEvaluacionExpandida);

    Evaluacion findByEvalExpSeccion(Long evaluacionExpansion, Long seccion);

    void updateDocenteEvaluador(Evaluacion evaluacion, Docente docente);

    List<Evaluacion> allByEvaluacionSeccion(EvaluacionSeccion evalSecc);

    List<Evaluacion> allByEvaluacionesByExpandidas(List<EvaluacionExpandida> evaluacionesExp);

    List<Evaluacion> allByEvaluacionExpandidaSecciones(EvaluacionExpandida evaluacion, List<Seccion> secciones);

    void deleteEvaluacionesByEvaluacionSeccion(EvaluacionSeccion evaluacionSeccion);

    List<Evaluacion> allByCiclo(CicloAcademico ciclo);

    List<Evaluacion> allByGrupoSeccionAlumno(GrupoSeccion grupoSeccion, Alumno alumno);
}
