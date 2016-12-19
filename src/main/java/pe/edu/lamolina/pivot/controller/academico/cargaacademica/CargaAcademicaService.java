package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.ReclamoNota;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CargaAcademicaService {

    List<TipoEvaluacion> allTipoEvaluacion();

    List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente, CicloAcademico ciclo);

    List<DocenteSeccion> allDocenteSeccionByDocente(Docente docente);

    List<EvaluacionPlan> allEvaluacionPlanByPlanCalifica(Long idPlanCalificacion);

    PlanCalificacion findPlanCalificacion(Long idPlanCalificacion);

    Curso findCurso(Long idCurso);

    Seccion findSeccion(Long idGrupoSeccion);

    GrupoSeccion findGrupo(Long idGrupoSeccion);

    List<EvaluacionPlan> allEvaluacionPlanByDynatable(DynatableFilter filter, Long idPlanCalificacion);

    List<EvaluacionExpandida> allEvaluacionesExpByEvalSeccion(EvaluacionSeccion evaluacionSeccion);

    List<AlumnoEvaluacion> allAlumnoEvaluacionByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion);

    EvaluacionPlan findEvaluacionPlan(Long idEvaluacionPlan);

    Evaluacion findEvaluacion(Long idEvaluacionPlan);

    EvaluacionExpandida findEvaluacionExpandida(Long idEvaluacionPlan);

    void deleteEvaluacionExpandida(Long id);

    void createEvaluacionSeccionPorDocente(Docente docente);

    void createEvaluacionExpPorEvalSeccion(EvaluacionSeccion evaluacionSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum);

    void saveExpansionEvaluacion(EvaluacionExpandida evaluacion, DataSessionPivot ds);

    EvaluacionSeccion findEvalSeccByPlanCalGrupoSec(Long idPlanCalificacion, Long idGrupoSeccion);

    EvaluacionSeccion findEvaluacionSeccion(Long id);

    List<Evaluacion> allEvaluacionesByEvalSeccion(EvaluacionSeccion evaluacionSeccion);

    List<SistemaNotas> allSistemasNotas();

    void saveSistemaCalifica(PlanCalificacion planCalificacion, Long grupoSeccionId);

    void aceptarExpansion(Long evaluacionSeccionId, DataSessionPivot ds);

    void aceptarRechazo(Long cursoId, Long seccionId, DataSessionPivot ds);

    void aceptarPlanCalificacion(Long cursoId, Long seccionId, DataSessionPivot ds);

    DocenteSeccion findDocenteSeccion(Long idDocenteSeccion);

    List<Evaluacion> allEvaluacionByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion);

    List<Evaluacion> findBySeccion(Long idSeccion);

    List<Evaluacion> allEvaluacionByEvaluacionSeccion(EvaluacionSeccion evaluacionSeccion);

    List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion);

    void updateEvaluacion(Evaluacion evaluacion);

    void saveIngresoNotas(DataSessionPivot ds, Evaluacion evaluacion, AlumnoEvaluacion[] alumnoEvaluaciones);

    SistemaNotas findSistemaNotaById(Long id);

    ObjectNode getDetalleEvaluacion(Long idEvaluacion, Long idDocenteSeccion);

    List<Evaluacion> allEvaluacionBySecciones(List<Seccion> secciones);

    Map<String, String> allAlumnoEvaluacionBySeccion(Long idSeccion);

    MatriculaSeccion findMatriculaSeccion(Long id);

    List<AlumnoEvaluacion> allEvaluacionsByFilter(Alumno alumno, Curso curso, CicloAcademico cicloAcademico);

    AlumnoEvaluacion findAlumnoEvaluacion(Long id, Long idEvaluacion, Long idAlumno);

    void saveReclamoNota(ReclamoNota reclamoNota, DataSessionPivot ds);

    Map<Long, MatriculaCurso> getMapMatriculasCursoByCicloCurso(CicloAcademico ciclo, Curso curso);

    List<Evaluacion> allEvaluacionesByTipoSeccion(EvaluacionSeccion evaluacionSeccion);
}
