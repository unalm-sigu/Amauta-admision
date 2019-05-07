package pe.edu.lamolina.pivot.controller.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.controller.academico.calculonotas.CalculoNotasService;
import pe.edu.lamolina.pivot.controller.academico.promedio.ContadorComponent;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.docente.notasacademicas.NotaAcademicaService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("test")
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    EvaluacionExpandidaDAO evaluacionExpandidaDAO;

    @Autowired
    EvaluacionDAO evaluacionDAO;

    @Autowired
    AlumnoEvaluacionDAO alumnoEvaluacionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    NotaAcademicaService notaAcademicaService;

    @Autowired
    CalculoNotasService calculoNotasService;

    @Autowired
    VisorCalculoNotas visorCalculoNotas;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    PromedioService promedioService;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    ContadorComponent contadorComponent;

    @Autowired
    TestService service;

    @ResponseBody
    @RequestMapping("crearEvaluacionByExp")
    public String crearEvaluacionByExp(@RequestParam("idGrupoSeccion") Long idGpoSecc) {

        GrupoSeccion grupoSeccion = notaAcademicaService.findGrupo(idGpoSecc);
        if (grupoSeccion.getEvaluacionSecciones().isEmpty()) {
            return "No tiene evaluacionSeccion";
        }
        if (grupoSeccion.getEvaluacionSecciones().size() > 1) {
            return "No tiene varias evaluacionSecciones";
        }

        EvaluacionSeccion evaluacionSeccion = grupoSeccion.getEvaluacionSecciones().get(0);
        PlanCalificacion planCalificacion = grupoSeccion.getPlanCalificacion();

        List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
        logger.debug("Cantidad de secciones para el grupo {}", secciones.size());
        List<EvaluacionExpandida> planEvaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null, null);
        logger.debug("Plan Calificacion {}, Cantidad de Evaluaciones {}", planCalificacion.getId(), planEvaluaciones.size());
        for (Seccion seccionEach : secciones) {
            for (EvaluacionExpandida evaluacionExpandida : planEvaluaciones) {
                logger.debug("Seccion Tipo {}", seccionEach.getTipoSeccionEnum().name());
                logger.debug("Tipo evaluacion en seccion {}", seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().name());
                logger.debug("Tipo Evaluacion {}", evaluacionExpandida.getTipoSeccionEvalEnum().name());
                if (seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
                        evaluacionExpandida.getTipoSeccionEvalEnum())) {

                    Evaluacion evaluacion = evaluacionDAO.findByEvalExpSeccion(evaluacionExpandida.getId(), seccionEach.getId());
                    if (evaluacion != null) {
                        continue;
                    }

                    evaluacion = new Evaluacion();
                    evaluacion.create(evaluacionSeccion, seccionEach, evaluacionExpandida);
                    if (evaluacionExpandida.getEvaluacionesExpandidas() != null && !evaluacionExpandida.getEvaluacionesExpandidas().isEmpty()) {
                        evaluacion.setEvaluaciones(new ArrayList<>());
                        for (EvaluacionExpandida evalExp : evaluacionExpandida.getEvaluacionesExpandidas()) {
                            Evaluacion evaluacionChild = new Evaluacion();
                            evaluacionChild.create(evaluacionSeccion, seccionEach, evalExp);
                            evaluacionChild.setEvaluacionSuperior(evaluacion);
                            evaluacion.getEvaluaciones().add(evaluacionChild);
                        }
                    }
                    notaAcademicaService.saveEvaluacion(evaluacion);

                }
            }
        }

        return "YEAH";

    }

    @ResponseBody
    @RequestMapping("/calcularAllResumenEvaluacion/{seccion}")
    public String calcularAllResumenEvaluacion(@PathVariable("seccion") Long seccionId, HttpSession session) {
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.calcularAllResumenEvaluacion(seccionId, ciclo, ds);

        } catch (Exception e) {
        }

        return "yeah";
    }

    @ResponseBody
    @RequestMapping("calcularAllResumenEvaluacion")
    public String calcularAllResumenEvaluacion(HttpSession session) {
        int loop = 1;
        visorCalculoNotas.iniciar();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<CicloAcademico> ciclosActivos = cicloAcademicoDAO.allActivos();

        for (CicloAcademico cicloActivo : ciclosActivos) {
            List<MatriculaSeccion> alumnosSeccion = matriculaSeccionDAO.allMatriculadosByCiclo(cicloActivo);
            for (MatriculaSeccion ms : alumnosSeccion) {
                Seccion seccion = ms.getSeccion();
                GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
                Alumno alumno = ms.getMatriculaResumen().getAlumno();

                if (gpoSecc.getPlanCalificacion() == null) {
                    continue;
                }

                if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                    continue;
                }

                if (!cicloActivo.isActivo()) {
                    if (seccion.getGrupoSeccion().isEstadoGrupoCerrado()) {
                        continue;
                    }
                }

                calculoNotasService.recalcularAllResumenEvalAlumno(alumno, gpoSecc, loop, ds);
                loop++;

            }
        }
        return "yeah";
    }

    @ResponseBody
    @RequestMapping("calcularAllPromediosByCiclo")
    public String calcularAllPromediosByCiclo(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByCiclo(ciclo);
        logger.debug("Catidad de registros a procesar {}", matriculasCurso.size());
        for (MatriculaCurso matriculaCurso : matriculasCurso) {
            //   if (matriculaCurso.getMatriculaResumen().getAlumno().getId().compareTo(54234L) == 0) {

            matriculaCurso.getMatriculaResumen().getAlumno();
            matriculaCurso.getMatriculaResumen().getCicloAcademico();
            matriculaCurso.getCurso();
            promedioService.trasladoPromediosSource(matriculaCurso, ds);

            //  }
        }

        return "yeah";
    }

    @ResponseBody
    @RequestMapping("promediarciclo/{ciclo}")
    public String promediarAll(@PathVariable("ciclo") Long cicloId, HttpSession session) {
        logger.info("promediarAll");
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();
        CicloAcademico cicloAcademico = cicloAcademicoDAO.find(new CicloAcademico(cicloId));
        List<MatriculaResumen> matriculasResumen = matriculaResumenDAO.allByCicloFull(cicloAcademico);
        logger.info("matriculas resumen encontradas {}", matriculasResumen.size());

        visorCalculoNotas.iniciar();
        visorCalculoNotas.setCantidadTotal(matriculasResumen.size());
        for (MatriculaResumen mResumen : matriculasResumen) {
            Alumno alumno = mResumen.getAlumno();
            List<AlumnoCicloCurso> alumnosCicloCursoByAlumno = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
            promedioService.promediarAllCicloAsync(alumno, cicloAcademico, ciclos, alumnosCicloCursoByAlumno, ds);
        }
        return "yeah";
    }

    @ResponseBody
    @RequestMapping("promediarfull")
    public String promediarfull(HttpSession session) {
        logger.info("promediarful");
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        List<String> allYears = alumnoDAO.allYearsCiclos();
        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();

        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(new ModalidadEstudio(1));
        List<Alumno> alumnosAcumulados = new ArrayList<>();
        for (String year : allYears) {
            /*
            if (TypesUtil.getInt(year) != null && TypesUtil.getInt(year) < 2015) {
                continue;
            }
             */
            List<Alumno> alumnos = alumnoDAO.allPendingPREPromedioByCicloYear(year);
            alumnosAcumulados.addAll(alumnos);
            logger.info("Año {}, Alumnos {}, Acumulados {}", year, alumnos.size(), alumnosAcumulados.size());
        }
        contadorComponent.iniciar(alumnosAcumulados.size());
        for (Alumno alumno : alumnosAcumulados) {
            promedioService.promediarAllCicloAsync(alumno, cicloActivo, ciclos, null, ds);
        }

        return "yeah";
    }

    @ResponseBody
    @RequestMapping("promediarepgfull")
    public String promediarepgfull(HttpSession session) {
        logger.info("promediarepgfull");
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<String> allYears = alumnoDAO.allYearsCiclos();
        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();

        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(new ModalidadEstudio(2));
        List<Alumno> alumnosAcumulados = new ArrayList<>();
        for (String year : allYears) {
            List<Alumno> alumnos = alumnoDAO.allPendingEpgPromedioByCicloYear(year);
            alumnosAcumulados.addAll(alumnos);
            logger.info("Año {}, Alumnos {}, Acumulados {}", year, alumnos.size(), alumnosAcumulados.size());
        }
        contadorComponent.iniciar(alumnosAcumulados.size());
        for (Alumno alumno : alumnosAcumulados) {
            promedioService.promediarAllCicloAsync(alumno, cicloActivo, ciclos, null, ds);
        }

        return "yeah";
    }

    @ResponseBody
    @RequestMapping("promediarciclocod/{ciclo}")
    public String promediarAll(@PathVariable("ciclo") String cicloCod, HttpSession session) {
        logger.info("promediarciclocod {}", cicloCod);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        visorCalculoNotas.iniciar();
        List<CicloAcademico> ciclosAll = cicloAcademicoDAO.all();
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allByCodigo(cicloCod);
        List<String> ciclosStr = ciclos.stream().map(x -> x.toString()).collect(Collectors.toList());
        logger.info("ciclos encontrados {}", String.join(",", String.join(",", ciclosStr)));

        List<CicloAcademico> ciclosActivos = cicloAcademicoDAO.allActivosAlModalidades();

        for (CicloAcademico cicloAcademico : ciclos) {
            if (!cicloAcademico.getModalidadEstudio().isPregrado()) {
                continue;
            }
            CicloAcademico cicloActivoByModalidad = ciclosActivos.stream()
                    .filter(x -> x.getModalidadEstudio().equals(cicloAcademico.getModalidadEstudio()))
                    .findFirst().orElse(null);
            List<MatriculaResumen> matriculasResumen = matriculaResumenDAO.allByCicloFull(cicloAcademico);
            logger.info("matriculas resumen encontradas {}, del ciclo {}", matriculasResumen.size(), cicloAcademico.toString());

            for (MatriculaResumen mResumen : matriculasResumen) {
                Alumno alumno = mResumen.getAlumno();
                promedioService.promediarAllCicloAsync(alumno, cicloActivoByModalidad, ciclosAll, null, ds);
            }
        }
        return "yeah";
    }

    @ResponseBody
    @RequestMapping("promediaralumno/{alumno}")
    public String calcularAllPromediosByCiclo(HttpSession session, @PathVariable("alumno") Long alumnoId) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        //  List<CicloAcademico> allCiclosActivos = cicloAcademicoDAO.allActivesByModalidad(alumno.getModalidadEstudio(), new String[]{"ca.year asc", "ca.numeroCiclo asc"});
        visorCalculoNotas.setActivo(false);
        ds.setFechaAccionAudit(new Date());
        promedioService.calulcarSituacionAcademica(new Alumno(alumnoId), ds);
        return "yeah";
    }

    //Trasladar informacion de matricula curso a alumnociclocurso
    @ResponseBody
    @RequestMapping("trasladarInformcionForHistorial")
    public String trasladarMatriculaCursoForPromedios(HttpSession session) {
        //201700
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        List<CicloAcademico> ciclos = cicloAcademicoDAO.allWithInitAndOrderBy(2017, "ca.codigo asc", CicloAcademicoEstadoEnum.ACT, CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);
        //   List<GrupoSeccion> gruposSeccionesByCiclo=gruposecc
        for (CicloAcademico cicloAcademico : ciclos) {
            List<MatriculaResumen> matriculasResumen = matriculaResumenDAO.allByCiclo(cicloAcademico);
            List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByCicloFull(cicloAcademico);
            if (matriculasResumen.isEmpty()) {
                continue;
            }
            List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allActivesByMatriculaResumen(matriculasResumen);
            if (matriculasSeccion.isEmpty()) {
                continue;
            }

            visorCalculoNotas.iniciar();
            visorCalculoNotas.setCantidadTotal(matriculasResumen.size());
            for (MatriculaResumen matriculaResumen : matriculasResumen) {
                promedioService.trasladarInformcionForHistorial(matriculaResumen, matriculasCurso, matriculasSeccion, ds, false);
            }
        }
        return "yeah";
    }

    @ResponseBody
    @RequestMapping("trasladarInformcionForHistorial/{alumno}")
    public String trasladarMatriculaCursoForPromedios(HttpSession session, @PathVariable("alumno") Long alumnoId) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        service.trasladarMatriculaCursoForPromedios(ds, alumnoId);
        return "yeah";
    }

}
