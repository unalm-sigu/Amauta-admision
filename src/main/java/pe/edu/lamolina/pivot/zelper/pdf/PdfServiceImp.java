package pe.edu.lamolina.pivot.zelper.pdf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.edu.lamolina.pivot.controller.academico.cargaacademica.CargaAcademicaService;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.enums.DocumentoPdfEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PdfServiceImp implements PdfService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    PdfGenerator pdfGenerator;
    
    @Autowired
    CargaAcademicaService cargaAcademicaService;
    
    @Autowired
    CursoDAO cursoDAO;
    
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    
    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;
    
    @Autowired
    PlanCalificacionDAO planCalificacionDAO;
    
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    
    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;
    
    @Autowired
    FacultadDAO facultadDAO;
    
    @Autowired
    DocenteDAO docenteDAO;
    
    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;
    
    public List<String> reporteDeActaDeNotas(Long idDocenteSeccion, DataSessionPivot ds) {
        //47

        List<String> pdfs = new ArrayList<>();
        
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        logger.debug("ciclo academico {}", cicloAcademico.getId());
        
        DocenteSeccion docenteSeccion = docenteSeccionDAO.find(idDocenteSeccion);
        
        GrupoSeccion grupoSeccion = grupoSeccionDAO.find(docenteSeccion.getSeccion().getGrupoSeccion().getId());
        Curso curso = cursoDAO.find(grupoSeccion.getCurso().getId());
        logger.debug("el curso {}", curso.getId());
        Seccion seccion = docenteSeccion.getSeccion();
        PlanCalificacion planCalificacion = planCalificacionDAO.find(grupoSeccion.getPlanCalificacion().getId());
        DepartamentoAcademico departamentoAcademico = departamentoAcademicoDAO.find(curso.getDepartamentoAcademico().getId());
        Facultad facultad = facultadDAO.find(departamentoAcademico.getId());
        
        List< MatriculaSeccion> matriculasSeccionByFilter = matriculaSeccionDAO.allBySeccion(docenteSeccion.getSeccion());
        logger.debug("matriculas seccion size {}", matriculasSeccionByFilter.size());
        Map<String, String> mapNotas = cargaAcademicaService.allAlumnoEvaluacionBySeccion(docenteSeccion.getSeccion().getId());
        
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allBySeccion(seccion);
        logger.debug("Seccion {}, Cantidad Docentes Seccion {}", seccion.getId(), docentesSeccion.size());
        Docente docentePrincipal = null;
        if (docentesSeccion.size() == 1) {
            docentePrincipal = docenteDAO.find(docentesSeccion.get(0).getDocente().getId());
        } else {
            for (DocenteSeccion docenteSeccion1 : docentesSeccion) {
                if (docenteSeccion1.esDocentePrincipal()) {
                    docentePrincipal = docenteDAO.find(docenteSeccion1.getDocente().getId());
                }
            }
        }
        
        int cantReg = 38;
        int ind = 0;
        List<MatriculaSeccion> lstMatriculaSeccion = new ArrayList<>();
        
        Map matriculaCursoMap = getMapMatriculasCursoByCicloCurso(cicloAcademico, curso);
        logger.debug("cantidad de matriculas cursos {}", matriculaCursoMap.size());
        
        for (MatriculaSeccion matriculaSeccion : matriculasSeccionByFilter) {
            String matricula = matriculaSeccion.getMatriculaResumen().getAlumno().getCodigo();
            String alumno = matriculaSeccion.getMatriculaResumen().getAlumno().getPersona().getApellidosNombres();
            // String notaFinal=matriculaSeccion.getMatriculaResumen().getMatriculaCurso().

            StringBuilder strb = new StringBuilder();
            for (EvaluacionPlan evaPlan : planCalificacion.getEvaluacionPlan()) {
                strb.append(evaPlan.getTipoEvaluacion().getNombre()).append(",");
            }
            String notafinal = "";
            if (matriculaCursoMap.get(matriculaSeccion.getMatriculaResumen().getAlumno().getId()) != null) {
                MatriculaCurso matriculaCurso = (MatriculaCurso) matriculaCursoMap.get(matriculaSeccion.getMatriculaResumen().getAlumno().getId());
                notafinal = matriculaCurso.getNotaFinal();
            }
            strb.append(", nota final ").append(notafinal);
            logger.debug("Matricula {}, Alumno {}, Evaluaciones {}", matricula,
                    matriculaSeccion.getMatriculaResumen().getAlumno().getId(), strb.toString());
            ind++;
            lstMatriculaSeccion.add(matriculaSeccion);
            if ((ind % cantReg == 0) || ind == matriculasSeccionByFilter.size()) {
                logger.debug("el docente es {}, {}", docentePrincipal.getCodigo(), docentePrincipal.getPersona().getApellidosNombres());
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
                ctx.setVariable("pagina", pdfs.size() + 1);
                
                ctx.setVariable("matriculaCurso", matriculaCursoMap);
                
                PdfContent pdfContent = new PdfContent();
                pdfContent.setDocumentPdfEnum(DocumentoPdfEnum.ACTA_NOTAS);
                pdfContent.setContext(ctx);
                
                String subFolder = "acta_notas";
                String filePdf = pdfGenerator.generateDocument(pdfContent, subFolder);
                pdfs.add(filePdf);
                lstMatriculaSeccion = new ArrayList<>();
            }
        }
        
        return pdfs;
    }
    
    @Override
    public String concatPDFs(List<String> pdfFilesStr, String outputStreamStr, boolean paginate) {
        return pdfGenerator.concatPDFs(pdfFilesStr, outputStreamStr, paginate);
    }
    
    private Map<Long, MatriculaCurso> getMapMatriculasCursoByCicloCurso(CicloAcademico ciclo, Curso curso) {
        List<MatriculaCurso> lstMatriculaCurso = matriculaCursoDAO.findByCursoCiclo(curso, ciclo);
        Map<Long, MatriculaCurso> resultMap = new HashMap<>();
        for (MatriculaCurso matriculaCurso : lstMatriculaCurso) {
            logger.debug(matriculaCurso.getMatriculaResumen().getAlumno().getId().toString());
            resultMap.put(matriculaCurso.getMatriculaResumen().getAlumno().getId(), matriculaCurso);
        }
        return resultMap;
    }
    
}
