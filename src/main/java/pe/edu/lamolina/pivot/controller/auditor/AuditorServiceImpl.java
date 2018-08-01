package pe.edu.lamolina.pivot.controller.auditor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import pe.edu.lamolina.pivot.controller.interceptor.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SistemaNotas;
import pe.edu.lamolina.model.enums.LoggerAccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AuditorServiceImpl implements AuditorService {

    @Autowired
    InterceptorService interceptorService;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    @Async
    public void auditSaveNotas(PlanCalificacion planCalificacion, SistemaNotas sistemaNotas, Seccion seccion, Curso curso,
            CicloAcademico cicloAcademico,
            List<Evaluacion> evaluacionesBySeccionFinal,
            List<MatriculaSeccion> matriculasSeccionByFilter,
            Map<String, AlumnoEvaluacion> notas,
            Map matriculaCursoMap,
            DataSessionPivot ds) {

        ObjectNode ingresoNotas = new ObjectNode(JsonNodeFactory.instance);
        ingresoNotas.put("seccionId", seccion.getId());
        ingresoNotas.put("seccionTipo", seccion.getTipoSeccion());
        ingresoNotas.put("seccionClave", seccion.getCodigo2());
        ingresoNotas.put("seccionTipo", seccion.getTipoSeccion());
        ingresoNotas.put("planCalificacionCodigo", planCalificacion.getCodigo());
        ingresoNotas.put("planCalificacionFormula", planCalificacion.getFormula());
        ingresoNotas.put("sistemaNotas", sistemaNotas.getNombre());
        ingresoNotas.put("sistemaNotas", sistemaNotas.getNombre());
        ingresoNotas.put("cursoNombre", curso.getCreditos());
        ingresoNotas.put("cursoCodigo", curso.getCodigo());
        ingresoNotas.put("cursoTipo", curso.getTipoCurso());
        ingresoNotas.put("cursoTipoCredito", curso.getTipoCredito());
        ingresoNotas.put("cursoCreditos", curso.getCreditos());
        ingresoNotas.put("cursoCreditosVariables", curso.getCreditosVariables());

        //  List<Evaluacion> evaluacionesBySeccionFinal = this.allEvaluacionesByTipoSeccion(seccion);
        //   List<MatriculaSeccion> matriculasSeccionByFilter = this.allMatriculaSeccionBySeccion(seccion);
        //    Map<String, AlumnoEvaluacion> notas = this.allAlumnoEvaluacionBySeccion(seccion.getId());
        //     Map matriculaCursoMap = this.getMapMatriculasCursoByCicloCurso(cicloAcademico, curso);
        ArrayNode arrayNotasNode = new ArrayNode(JsonNodeFactory.instance);
        for (MatriculaSeccion matriculaSeccion : matriculasSeccionByFilter) {
            ObjectNode notasNode = new ObjectNode(JsonNodeFactory.instance);
            notasNode.put("alumno", matriculaSeccion.getMatriculaResumen().getAlumno().getPersona().getApellidosNombres());
            notasNode.put("alumnoCodigo", matriculaSeccion.getMatriculaResumen().getAlumno().getCodigo());

            for (Evaluacion evaluacion : evaluacionesBySeccionFinal) {
                StringBuilder evaluacionText = new StringBuilder();
                evaluacionText.append(evaluacion.getTipoEvaluacion().getCodigo()).append(evaluacion.getNumero());

                StringBuilder key = new StringBuilder();
                key.append(matriculaSeccion.getMatriculaResumen().getAlumno().getId()).append("-").append(evaluacion.getId());
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
                if (sistemaNotas.isLetras()) {
                    notasNode.put("creditosMatriculados", matriculaCurso.getCreditos());
                } else if (sistemaNotas.isNumerico()) {
                    notasNode.put("notaAvance", matriculaCurso.getNotaAvanceFull());
                    notasNode.put("notaAcumulada", matriculaCurso.getNotaAcumuladaFull());
                    notasNode.put("notaFinal", matriculaCurso.getNotaFinal());
                }
            }
            arrayNotasNode.add(notasNode);
        }
        ingresoNotas.set("notas", arrayNotasNode);

        ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
        objNode.put("tipo", LoggerAccionEnum.GRABAR_NOTAS_ACADEMICAS.name());
        objNode.set("data", ingresoNotas);
        interceptorService.saveInterceptor(objNode, ds);
    }

}
