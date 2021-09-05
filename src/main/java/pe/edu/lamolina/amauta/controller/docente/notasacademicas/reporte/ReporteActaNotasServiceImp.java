package pe.edu.lamolina.amauta.controller.docente.notasacademicas.reporte;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EvaluacionPlan;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.ResumenAlumnoEvaluacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoEvaluacion;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.ResumenAlumnoEvaluacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.controller.docente.notasacademicas.NotaAcademicaService;
import pe.edu.lamolina.amauta.dao.academico.EvaluacionPlanDAO;

@Service
@Transactional(readOnly = true)
public class ReporteActaNotasServiceImp implements ReporteActaNotasService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NotaAcademicaService notaAcademicaService;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    ResumenAlumnoEvaluacionDAO resumenAlumnoEvaluacionDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    EvaluacionPlanDAO evaluacionPlanDAO;

    @Override
    public List<Context> reporteDeActaDeNotas(Long idGrupoSeccion, DataSessionPivot ds) {

        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        GrupoSeccion grupoSeccion = grupoSeccionDAO.find(idGrupoSeccion);
        Curso curso = grupoSeccion.getCurso();
        PlanCalificacion planCalificacion = grupoSeccion.getPlanCalificacion();
        List<EvaluacionPlan> evaluaciones = new ArrayList();
        if (planCalificacion != null) {
            evaluaciones = evaluacionPlanDAO.allByPlan(planCalificacion);
            planCalificacion.setEvaluacionPlan(evaluaciones);
        }
        DepartamentoAcademico departamentoAcademico = curso.getDepartamentoAcademico();
        Facultad facultad = departamentoAcademico.getFacultad();

        Collections.sort(evaluaciones, (p1, p2) -> p1.getTipoEvaluacion().getOrden().compareTo(p2.getTipoEvaluacion().getOrden()));

        Seccion seccion = null;
        Docente docentePrincipal = null;

        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allByGrupoSeccion(grupoSeccion);
        for (DocenteSeccion docSecc : docentesSeccion) {
            if (!docSecc.isEstadoActivado()) {
                continue;
            }
            Seccion secc = docSecc.getSeccion();
            if (secc.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }
            seccion = secc;
            docentePrincipal = docSecc.getDocente();
            if (docSecc.esDocentePrincipal()) {
                docentePrincipal = docSecc.getDocente();
                break;
            }
        }

        List<MatriculaSeccion> matriculasSeccionByFilter = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
        List<ResumenAlumnoEvaluacion> resumenesAlumnos = resumenAlumnoEvaluacionDAO.allByGrupoSeccion(grupoSeccion);
        Map<String, ResumenAlumnoEvaluacion> mapNotas = mapearNotas(resumenesAlumnos);

        int cantReg = 38;
        int ind = 0;
        List<MatriculaSeccion> lstMatriculaSeccion = new ArrayList<>();

        Map matriculaCursoMap = notaAcademicaService.getMapMatriculasCursoByCicloCurso(cicloAcademico, curso);
        
        List<Context> multipleContext= new ArrayList();

        for (MatriculaSeccion matriculaSeccion : matriculasSeccionByFilter) {
            ind++;
            lstMatriculaSeccion.add(matriculaSeccion);
            if ((ind % cantReg == 0) || ind == matriculasSeccionByFilter.size()) {

                Context ctx = new Context();
                ctx.setVariable("planCalificacion", planCalificacion);
                ctx.setVariable("lstMatriculasSeccion", lstMatriculaSeccion);
                ctx.setVariable("notas", mapNotas);
                ctx.setVariable("cicloAcademico", cicloAcademico);
                ctx.setVariable("seccion", seccion);
                ctx.setVariable("curso", curso);
                ctx.setVariable("departamentoAcademico", departamentoAcademico);
                ctx.setVariable("facultad", facultad);
                ctx.setVariable("docente", docentePrincipal);

                DateTime today = new DateTime();
                ctx.setVariable("fecha", today.toString("dd/MM/yyyy"));
                ctx.setVariable("hora", today.toString("HH:mm:ss "));
                ctx.setVariable("pagina", multipleContext.size() + 1);

                ctx.setVariable("matriculaCurso", matriculaCursoMap);

                if (matriculasSeccionByFilter.size() <= cantReg
                        || matriculasSeccionByFilter.size() == ind) {
                    ctx.setVariable("ultimaPagina", true);

                    SimpleDateFormat sdf = new SimpleDateFormat("'Lima, ' dd 'de' MMMMM 'del' yyyy", new Locale("es", "ES"));
                    String fecha = sdf.format(today.toDate());
                    ctx.setVariable("fechaCompleta", fecha);

                }

                multipleContext.add(ctx);
            }
        }

        return multipleContext;
    }

    private Map<String, ResumenAlumnoEvaluacion> mapearNotas(List<ResumenAlumnoEvaluacion> resumenesAlumnos) {
        Map<String, ResumenAlumnoEvaluacion> mapNotas = new LinkedHashMap();
        for (ResumenAlumnoEvaluacion rae : resumenesAlumnos) {
            Alumno alumno = rae.getAlumno();
            TipoEvaluacion tipo = rae.getTipoEvaluacion();
            mapNotas.put(alumno.getId() + "-" + tipo.getId(), rae);

        }
        return mapNotas;
    }

}
