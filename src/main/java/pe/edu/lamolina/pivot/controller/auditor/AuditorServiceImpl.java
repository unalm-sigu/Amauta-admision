package pe.edu.lamolina.pivot.controller.auditor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import pe.edu.lamolina.pivot.controller.interceptor.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SistemaNotas;
import pe.edu.lamolina.model.enums.LoggerAccionEnum;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AuditorServiceImpl implements AuditorService {

    @Autowired
    InterceptorService interceptorService;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    @Async
    public void auditTrasladoNotasToHistorial(Alumno alumno, Curso curso, CicloAcademico ciclo, MatriculaCurso matriculaCurso, DataSessionPivot ds, Exception exc) {
        JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
        ObjectNode objNode = new ObjectNode(jsonNodeFactory);

        ObjectNode data = new ObjectNode(jsonNodeFactory);
        data.put("alumnoId", alumno.getId());
        data.put("cursoId", curso.getId());
        data.put("cicloEvaluando", ciclo.toString());
        data.put("matriculaCursoId", matriculaCurso.getId());

        objNode.put("tipo", LoggerAccionEnum.TRASLADO_NOTAS_A_HISTORIAL.name());
        objNode.set("data", data);
        interceptorService.saveInterceptor(objNode, exc, ds);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    @Async
    public void auditPromediarAlumno(Alumno alumno, CicloAcademico cicloAcademico, DataSessionPivot ds, Exception exc) {
        JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
        ObjectNode objNode = new ObjectNode(jsonNodeFactory);

        ObjectNode data = new ObjectNode(jsonNodeFactory);
        data.put("alumnoId", alumno.getId());
        data.put("alumnoCodigo", alumno.getCodigo());
        data.put("cicloEvaluando", cicloAcademico.toString());

        objNode.put("tipo", LoggerAccionEnum.PROMEDIAR_ALUMNO.name());
        objNode.set("data", data);
        interceptorService.saveInterceptor(objNode, exc, ds);
    }

    @Async
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void auditSaveNotas(
            LoggerAccionEnum loggerAccionEnum,
            Evaluacion evaluacion,
            PlanCalificacion planCalificacion,
            SistemaNotas sistemaNotas,
            Seccion seccion, Curso curso,
            CicloAcademico cicloAcademico,
            List<Evaluacion> evaluacionesBySeccionFinal,
            List<MatriculaSeccion> matriculasSeccionByFilter,
            Map<String, AlumnoEvaluacion> notas,
            Map matriculaCursoMap,
            DataSessionPivot ds) {

        ObjectNode ingresoNotas = new ObjectNode(JsonNodeFactory.instance);

        ingresoNotas.put("planCalificacionCodigo", planCalificacion.getCodigo());
        ingresoNotas.put("planCalificacionFormula", planCalificacion.getFormula());
        ingresoNotas.put("sistemaNotas", sistemaNotas.getNombre());
        ingresoNotas.put("sistemaNotas", sistemaNotas.getNombre());
        ingresoNotas.put("cursoNombre", curso.getNombre());
        ingresoNotas.put("cursoCodigo", curso.getCodigo());
        ingresoNotas.put("cursoTipo", curso.getTipoCurso());
        ingresoNotas.put("cursoTipoCredito", curso.getTipoCredito());
        ingresoNotas.put("cursoCreditos", curso.getCreditos());
        ingresoNotas.put("cursoCreditosVariables", curso.getCreditosVariables());

        ingresoNotas.put("grupoSeccionId", seccion.getGrupoSeccion().getId());
        ingresoNotas.put("grupoSeccionCodigo", seccion.getGrupoSeccion().getCodigo());
        ingresoNotas.put("grupoSeccionCodigo2", seccion.getGrupoSeccion().getCodigo200());
        ingresoNotas.put("grupoSeccionEstadoGrupo", seccion.getGrupoSeccion().getEstadoGrupo());
        ingresoNotas.put("grupoSeccionEstadoPlan", seccion.getGrupoSeccion().getEstadoPlan());
        ingresoNotas.put("grupoSeccionVersion", seccion.getGrupoSeccion().getVersion());

        ingresoNotas.put("seccionId", seccion.getId());
        ingresoNotas.put("seccionTipo", seccion.getTipoSeccion());
        ingresoNotas.put("seccionClave", seccion.getCodigo2());
        ingresoNotas.put("seccionTipo", seccion.getTipoSeccion());

        DocenteSeccion docenteSeccion = docenteSeccionDAO.findWithPersonaByDocenteSeccion(evaluacion.getDocenteEvaluador(), evaluacion.getSeccionResponsable());
        if (docenteSeccion != null) {
            ingresoNotas.put("docenteEvaluador", docenteSeccion.getDocente().getPersona().getApellidosNombres());
            ingresoNotas.put("docenteEstado", docenteSeccion.getDocente().getEstado());

            ingresoNotas.put("docenteSeccionEstado", docenteSeccion.getPorcentajeCarga());
            ingresoNotas.put("docenteSeccionPorcentajeCarga", docenteSeccion.getPorcentajeCarga());
            ingresoNotas.put("docenteSeccionPrincipal", docenteSeccion.getPrincipal());
        }

        ingresoNotas.put("evaluacionId", evaluacion.getId());
        ingresoNotas.put("evaluacion", evaluacion.getTipoEvaluacion().getNombre() + evaluacion.getNumero());
        ingresoNotas.put("evaluacionFechaRealizada", TypesUtil.getStringDate(evaluacion.getFechaRealizada(), "dd/MM/yyyy"));
        ingresoNotas.put("evaluacionPeso", evaluacion.getPeso());

        //  List<Evaluacion> evaluacionesBySeccionFinal = this.allEvaluacionesByTipoSeccion(seccion);
        //   List<MatriculaSeccion> matriculasSeccionByFilter = this.allMatriculaSeccionBySeccion(seccion);
        //    Map<String, AlumnoEvaluacion> notas = this.allAlumnoEvaluacionBySeccion(seccion.getId());
        //     Map matriculaCursoMap = this.getMapMatriculasCursoByCicloCurso(cicloAcademico, curso);
        for (Map.Entry<String, AlumnoEvaluacion> entry : notas.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue().getId());
        }

        ArrayNode arrayNotasNode = new ArrayNode(JsonNodeFactory.instance);
        for (MatriculaSeccion matriculaSeccion : matriculasSeccionByFilter) {
            ObjectNode notasNode = new ObjectNode(JsonNodeFactory.instance);
            notasNode.put("alumno", matriculaSeccion.getMatriculaResumen().getAlumno().getPersona().getApellidosNombres());
            notasNode.put("alumnoCodigo", matriculaSeccion.getMatriculaResumen().getAlumno().getCodigo());

            System.out.println("matricula=" + matriculaSeccion.getMatriculaResumen().getAlumno().getCodigo());

            for (Evaluacion evaluacionEach : evaluacionesBySeccionFinal) {
                System.out.println("evaludacion.id=" + evaluacionEach.getId());
                StringBuilder evaluacionText = new StringBuilder();
                evaluacionText.append(evaluacion.getTipoEvaluacion().getCodigo()).append(evaluacionEach.getNumero());

                StringBuilder key = new StringBuilder();
                key.append(matriculaSeccion.getMatriculaResumen().getAlumno().getId()).append("-").append(evaluacionEach.getId());
                AlumnoEvaluacion alumnoEvaluacion = notas.get(key.toString());

                //nota
                StringBuilder strbNota = new StringBuilder();

                if (StringUtils.isNotBlank(alumnoEvaluacion.getValorLetra())) {
                    strbNota.append(alumnoEvaluacion.getValorLetra());
                }
                if (!curso.isCreditosZero()) {
                    strbNota.append(alumnoEvaluacion.getNota());
                }
                notasNode.put(evaluacionText.toString(), strbNota.toString());
            }
            MatriculaCurso matriculaCurso = (MatriculaCurso) matriculaCursoMap.get(matriculaSeccion.getMatriculaResumen().getAlumno().getId());
            if (matriculaCurso != null) {
                //creditos matriculados
                notasNode.put("matCursoPorcentajeAvance", matriculaCurso.getPorcentajeAvanceNota());
                notasNode.put("matCursoNotaAvance", matriculaCurso.getNotaAvance());
                notasNode.put("matCursoNotaAcumulada", matriculaCurso.getNotaAcumulada());
                if (sistemaNotas.isLetras()) {
                    notasNode.put("matCursoCreditosAprobados", matriculaCurso.getCreditosAprobados());
                    notasNode.put("matCursoCreditos", matriculaCurso.getCreditos());
                } else if (sistemaNotas.isNumerico()) {
                    notasNode.put("matCursoNotaAvance", matriculaCurso.getNotaAvanceFull());
                    notasNode.put("matCursoNotaAcumulada", matriculaCurso.getNotaAcumuladaFull());
                    notasNode.put("matCursoNotaFinal", matriculaCurso.getNotaFinal());
                }
            }
            arrayNotasNode.add(notasNode);
        }
        ingresoNotas.set("notas", arrayNotasNode);

        ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
        objNode.put("tipo", loggerAccionEnum.name());
        objNode.set("data", ingresoNotas);
        interceptorService.saveInterceptor(objNode, ds);
    }

    @Async
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void auditSaveNotas(
            Evaluacion evaluacion,
            PlanCalificacion planCalificacion,
            SistemaNotas sistemaNotas,
            Seccion seccion, Curso curso,
            CicloAcademico cicloAcademico,
            List<Evaluacion> evaluacionesBySeccionFinal,
            List<MatriculaSeccion> matriculasSeccionByFilter,
            Map<String, AlumnoEvaluacion> notas,
            Map matriculaCursoMap,
            DataSessionPivot ds) {

        this.auditSaveNotas(LoggerAccionEnum.GRABAR_NOTAS_ACADEMICAS, evaluacion, planCalificacion, sistemaNotas, seccion, curso, cicloAcademico, evaluacionesBySeccionFinal, matriculasSeccionByFilter, notas, matriculaCursoMap, ds);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    @Async
    public void saveGrupoSeccion(GrupoSeccion gsec, DataSessionPivot ds) {
        ObjectNode jGrupoSeccion = JsonHelper.createJson(gsec, JsonNodeFactory.instance, false,
                new String[]{
                    "codigo",
                    "codigo2",
                    "cursoDirigido",
                    "curso.id",
                    "curso.codigo",
                    "anexoBoletin.codigo",
                    "anexoBoletin.nombre"
                }
        );
        jGrupoSeccion.put("usuarioRegistro", ds.getUsuario().getPersona().getApellidosNombres());
        jGrupoSeccion.put("fechaRegistro", TypesUtil.getStringDate(ds.getFechaAccionAudit(), "dd/MM/yyyy H:mm:ss"));

        ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
        objNode.put("tipo", LoggerAccionEnum.GRUPO_SECCION_CRE.name());
        objNode.set("data", jGrupoSeccion);
        interceptorService.saveInterceptor(objNode, ds);
    }
}
