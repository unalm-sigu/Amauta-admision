package pe.edu.lamolina.amauta.zelper.pdf;

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
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AnexoBoletin;
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
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.amauta.dao.academico.ResumenAlumnoEvaluacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.controller.docente.notasacademicas.NotaAcademicaService;
import pe.edu.lamolina.amauta.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.amauta.dao.academico.EvaluacionPlanDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;

@Service
@Transactional(readOnly = true)
public class PdfServiceImp implements PdfService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PdfGenerator pdfGenerator;

    @Autowired
    NotaAcademicaService notaAcademicaService;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

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

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    EvaluacionPlanDAO evaluacionPlanDAO;

    @Override
    public List<String> reporteDeActaDeNotas(Long idGrupoSeccion, DataSessionPivot ds) {
        //47

        List<String> pdfs = new ArrayList<>();

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
        //  ds.getDocente()
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
                pdfContent.setTipoPdfEnum(TipoPdfEnum.ACTA_NOTAS);
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

//    @Override
//    public List<String> reporteProgramacion(CicloAcademico ciclo) {
//        List<String> pdfs = new ArrayList<>();
//        List<AnexoBoletin> anexos = anexoBoletinDAO.allTodosByCiclo(ciclo);
//        for (AnexoBoletin anexo : anexos) {
//            anexo.setGruposSecciones(new ArrayList());
//        }
//        Map<Long, AnexoBoletin> mapAnexoSuper = TypesUtil.convertListToMap("anexoSuperior.id", "anexoSuperior", anexos);
//        Map<Long, AnexoBoletin> mapAnexos = TypesUtil.convertListToMap("id", anexos);
//
//        List<AnexoBoletin> anexosSuper = new ArrayList(mapAnexoSuper.values());
//        for (AnexoBoletin anexo : anexosSuper) {
//            anexo.setAnexosBoletinHijos(new ArrayList());
//        }
//        for (AnexoBoletin anexo : anexos) {
//            AnexoBoletin anexoPadre = mapAnexoSuper.get(anexo.getAnexoSuperior().getId());
//            anexoPadre.getAnexosBoletinHijos().add(anexo);
//            anexo.setAnexoSuperior(anexoPadre);
//        }
//
//        List<Seccion> secciones = seccionDAO.allForBoletinByCiclo(ciclo);
//        Map<Long, Seccion> mapSeccion = TypesUtil.convertListToMap("id", secciones);
//        for (Seccion secc : secciones) {
//            secc.setDocenteSeccion(new ArrayList());
//        }
//
//        Map<Long, GrupoSeccion> mapGpoSeccion = TypesUtil.convertListToMap("grupoSeccion.id", "grupoSeccion", secciones);
//        List<GrupoSeccion> gpoSecciones = new ArrayList(mapGpoSeccion.values());
//        for (GrupoSeccion gpoSecc : gpoSecciones) {
//            gpoSecc.setSecciones(new ArrayList());
//            AnexoBoletin anexo = mapAnexos.get(gpoSecc.getAnexoBoletin().getId());
//            anexo.getGruposSecciones().add(gpoSecc);
//        }
//
//        for (Seccion secc : secciones) {
//            GrupoSeccion gpoSecc = mapGpoSeccion.get(secc.getGrupoSeccion().getId());
//            gpoSecc.getSecciones().add(secc);
//            secc.setGrupoSeccion(gpoSecc);
//        }
//
//        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allActivosBySecciones(secciones);
//        for (DocenteSeccion profeSecc : docentesSecciones) {
//            Seccion secc = mapSeccion.get(profeSecc.getSeccion().getId());
//            secc.getDocenteSeccion().add(profeSecc);
//            profeSecc.setSeccion(secc);
//        }
//
//        String pdf = createPdfReporteProgramacion(anexosSuper);
//        pdfs.add(pdf);
//
//        return pdfs;
//    }

    private String createPdfReporteProgramacion(List<AnexoBoletin> listAB) {

        Context ctx = new Context();
        ctx.setVariable("listAB", listAB);

        //   ctx.setVariable("page", (index + 1));
        PdfContent pdfContent = new PdfContent();
        pdfContent.setTipoPdfEnum(TipoPdfEnum.PROGRAMACION_HORARIOS);
        pdfContent.setContext(ctx);
//        String nombre = modalidad.getNombre().replace(' ', '_').replace('-', '_');
//        String subFolder = nombre + "_" + index;
//        String filePdf = pdfGenerator.generateDocument(pdfContent, subFolder);
        String filePdf = pdfGenerator.generateDocument(pdfContent);
        return filePdf;
    }
}
