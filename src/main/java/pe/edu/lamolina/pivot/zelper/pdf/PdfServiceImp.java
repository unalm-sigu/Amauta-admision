package pe.edu.lamolina.pivot.zelper.pdf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import pe.edu.lamolina.pivot.dao.academico.ResumenAlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.ResumenAlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.enums.DocumentoPdfEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;
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
    @Autowired
    ResumenAlumnoEvaluacionDAO resumenAlumnoEvaluacionDAO;

    @Override
    public List<String> reporteDeActaDeNotas(Long idGrupoSeccion, DataSessionPivot ds) {
        //47

        List<String> pdfs = new ArrayList<>();

        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        GrupoSeccion grupoSeccion = grupoSeccionDAO.find(idGrupoSeccion);
        Curso curso = grupoSeccion.getCurso();
        PlanCalificacion planCalificacion = grupoSeccion.getPlanCalificacion();
        DepartamentoAcademico departamentoAcademico = curso.getDepartamentoAcademico();
        Facultad facultad = departamentoAcademico.getFacultad();

        Collections.sort(planCalificacion.getEvaluacionPlan(), (p1, p2) -> p1.getTipoEvaluacion().getOrden().compareTo(p2.getTipoEvaluacion().getOrden()));

        Seccion seccion = null;
        Docente docentePrincipal = null;
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allByGrupoSeccion(grupoSeccion);
        for (DocenteSeccion docSecc : docentesSeccion) {
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

        List<MatriculaSeccion> matriculasSeccionByFilter = matriculaSeccionDAO.allBySeccion(seccion);
        List<ResumenAlumnoEvaluacion> resumenesAlumnos = resumenAlumnoEvaluacionDAO.allByGrupoSeccion(grupoSeccion);
        Map<String, ResumenAlumnoEvaluacion> mapNotas = mapearNotas(resumenesAlumnos);

        int cantReg = 38;
        int ind = 0;
        List<MatriculaSeccion> lstMatriculaSeccion = new ArrayList<>();

        Map matriculaCursoMap = cargaAcademicaService.getMapMatriculasCursoByCicloCurso(cicloAcademico, curso);

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
                ctx.setVariable("pagina", pdfs.size() + 1);

                ctx.setVariable("matriculaCurso", matriculaCursoMap);

                if (matriculasSeccionByFilter.size() <= cantReg
                        || matriculasSeccionByFilter.size() == ind) {
                    ctx.setVariable("ultimaPagina", true);

                    SimpleDateFormat sdf = new SimpleDateFormat("'Lima, ' dd 'de' MMMMM 'del' yyyy", new Locale("es", "ES"));
                    String fecha = sdf.format(today.toDate());
                    ctx.setVariable("fechaCompleta", fecha);

                }

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
