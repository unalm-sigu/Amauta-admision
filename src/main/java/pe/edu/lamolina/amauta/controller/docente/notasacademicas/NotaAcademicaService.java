package pe.edu.lamolina.amauta.controller.docente.notasacademicas;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.EvaluacionPlan;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.model.academico.ReclamoNota;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SistemaNotas;
import pe.edu.lamolina.model.academico.TipoEvaluacion;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.LoggerAccionEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEvalEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface NotaAcademicaService {

    List<GrupoSeccion> allGrupoByDocente(Docente docente, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<TipoEvaluacion> allTipoEvaluacion();

    List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente, CicloAcademico ciclo);

    List<DocenteSeccion> allDocenteSeccionByDocente(Docente docente, CicloAcademico ciclo);

    List<EvaluacionPlan> allEvaluacionPlanByPlanCalifica(Long idPlanCalificacion);

    PlanCalificacion findPlanCalificacion(Long idPlanCalificacion);

    Curso findCurso(Long idCurso);

    Seccion findSeccion(Long idSeccion);

    GrupoSeccion findGrupo(Long idGrupoSeccion);

    List<DocenteSeccion> allDocenteSeccionByGrupo(GrupoSeccion grupoSeccion);

    List<EvaluacionPlan> allEvaluacionPlanByDynatable(DynatableFilter filter, Long idPlanCalificacion);

    List<EvaluacionExpandida> allEvaluacionesExpByEvalSeccion(EvaluacionSeccion evaluacionSeccion);

    List<Evaluacion> allEvaluacionesByEvalExpandida(EvaluacionExpandida evaluacionExpandida);

    List<AlumnoEvaluacion> allAlumnoEvaluacionByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idEvaluacion);

    EvaluacionPlan findEvaluacionPlan(Long idEvaluacionPlan);

    Evaluacion findEvaluacion(Long idEvaluacionPlan);

    EvaluacionExpandida findEvaluacionExpandida(Long idEvaluacionPlan);

    void deleteEvaluacionExpandida(Long id);

    void createEvaluacionSeccionPorDocente(Docente docente, CicloAcademico ciclo);

    void createEvaluacionExpPorEvalSeccion(EvaluacionSeccion evaluacionSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum, Date fechaRegistro, Usuario usuarioRegistro);

    void saveExpansionEvaluacion(EvaluacionExpandida evaluacion, DataSessionPivot ds);

    EvaluacionSeccion findEvalSeccByPlanCalGrupoSec(Long idPlanCalificacion, Long idGrupoSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum);

    EvaluacionSeccion findEvaluacionSeccion(Long id);

    List<Evaluacion> allEvaluacionesByEvalSeccion(EvaluacionSeccion evaluacionSeccion);

    List<SistemaNotas> allSistemasNotas();

    void saveSistemaCalifica(PlanCalificacion planCalificacion, Long grupoSeccionId, DataSessionPivot ds);

    void aceptarExpansion(Long evaluacionSeccionId, DataSessionPivot ds);

    void aceptarRechazo(Long cursoId, Long grupoId, DataSessionPivot ds);

    void aceptarPlanCalificacionSession(PlanCalificacion planCalificacion, Long cursoId, Long grupoId, DataSessionPivot ds);

    void aceptarPlanCalificacion(PlanCalificacion planCalificacion, Long cursoId, Long grupoId, DataSessionPivot ds);

    DocenteSeccion findDocenteSeccion(Long idDocenteSeccion);

    List<Evaluacion> allEvaluacionByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion);

    List<Evaluacion> findBySeccion(Long idSeccion);

    List<Evaluacion> allEvaluacionByEvaluacionSeccion(Seccion seccion);

    List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion);

    Evaluacion activarEvaluacion(Long evaluacionId, Date fechaRealizada, DataSessionPivot ds);

    void updateEvaluacion(Evaluacion evaluacion);

    List<MatriculaSeccion> saveIngresoNotas(Evaluacion evaluacion, AlumnoEvaluacion[] alumnoEvaluaciones, DataSessionPivot ds);

    void calcularNotasLista(List<MatriculaSeccion> matriculasSeccion, DataSessionPivot ds);

    SistemaNotas findSistemaNotaById(Long id);

    ObjectNode getDetalleEvaluacion(Long idEvaluacion, Long idSeccion);

    List<Evaluacion> allEvaluacionBySecciones(List<Seccion> secciones);

    Map<String, AlumnoEvaluacion> allAlumnoEvaluacionBySeccion(Long idSeccion);

    MatriculaSeccion findMatriculaSeccion(Long id);

    List<AlumnoEvaluacion> allEvaluacionsByFilter(Alumno alumno, Curso curso, CicloAcademico cicloAcademico, CicloAcademico academicoMOD);

    AlumnoEvaluacion findAlumnoEvaluacion(Long id, Long idEvaluacion, Long idAlumno);

    void saveReclamoNota(ReclamoNota reclamoNota, DataSessionPivot ds);

    Map<Long, MatriculaCurso> getMapMatriculasCursoByCicloCurso(CicloAcademico ciclo, Curso curso);

    List<Evaluacion> allEvaluacionesByTipoSeccion(Seccion seccion);

    void saveAsignacionDocentes(EvaluacionExpandida evaluacion, DataSessionPivot ds);

    DocenteSeccion findDocenteSeccionByFilter(Docente docente, Seccion seccion);

    public void saveEvaluacion(Evaluacion evaluacion);

    void deletePlanCalificacion(Long idPlanCalifica, DataSessionPivot ds);

    void cambiarTipoSeccionEvaluacion(EvaluacionExpandida evaluacionExpandida, TipoSeccionEvalEnum tipoSeccionEvalEnum);

    void saveAceptarExpandir(EvaluacionExpandida[] evaluacionesExpandidas);

    List<Curso> allActiveCursosByPlan(PlanCalificacion planCalificacion);

    String saveCerrarActa(GrupoSeccion grupoSeccion, DataSessionPivot ds);

    void calcularPromedios(GrupoSeccion grupoSeccion, DataSessionPivot ds, String token);

    void revisarCurriculaAlumnos(GrupoSeccion grupoSeccion, DataSessionPivot ds, String token);

    void revisarMatriculables(GrupoSeccion grupoSeccion, DataSessionPivot ds, String token);

    void desvincularPlanCalificacion(GrupoSeccion grupo);

    List<PlanCalificacionCurso> allActivosPlanCalificacionCurso(Curso curso, TipoCicloEnum tipoCicloEnum);

    void cambiarAnularNotaminima(EvaluacionExpandida evaluacionExpandida, Integer notaMinimaAnulable);

    List<MatriculaSeccion> eliminarNotas(Evaluacion evaluacion, DataSessionPivot ds);

    List<AlumnoEvaluacion> allAlumnosEvaluacionesPorEvaluacionExpandida(Long idEvaluacionExpandida);

    void anularEvaluacionExp(EvaluacionExpandida evaluacionExpandidaAnul);

    List<MatriculaSeccion> allMatriculaSeccionByFilter(EvaluacionExpandida evaluacionExpandida, CicloAcademico cilo);

    List<MatriculaCurso> allMatriculaCursoCiclo(Curso curso, CicloAcademico cicloAcademico);

    void saveEstructuraEvaluacion(GrupoSeccion grupoSeccion, LoggerAccionEnum loggerAccionEnum, HttpSession session);

    void saveEstructuraEvaluacion(EvaluacionExpandida evaluacionExpandida, LoggerAccionEnum loggerAccionEnum, HttpSession session);

    CicloAcademico findCicloConfOrAct(CicloAcademico cicloAcademico);

    MatriculaCurso findByCursoResumen(MatriculaResumen matriculaResumen, Curso curso);

    public String reenviarNotas(GrupoSeccion grupoSeccion, DataSessionPivot ds);

}
