package pe.edu.lamolina.pivot.controller.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import pe.albatross.zelpers.miscelanea.TypesUtil;
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
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_5;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_EM;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_N;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_TU;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.controller.academico.calculonotas.CalculoNotasService;
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
        int loop = 1;
        visorCalculoNotas.iniciar();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        Seccion seccionPV = seccionDAO.find(seccionId);

        List<MatriculaSeccion> alumnosSeccion = matriculaSeccionDAO.allMatriculadosByGpoSeccion(seccionPV.getGrupoSeccion(), ciclo);
        for (MatriculaSeccion ms : alumnosSeccion) {
            Seccion seccion = ms.getSeccion();
            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
            Alumno alumno = ms.getMatriculaResumen().getAlumno();

            if (gpoSecc.getPlanCalificacion() == null) {
                break;
            }

            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }

            calculoNotasService.recalcularAllResumenEvalAlumno(alumno, gpoSecc, loop, ds);
            loop++;

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
    @RequestMapping("promediarAll")
    public String promediarAll(HttpSession session) {
        logger.info("promediarAll");
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        ModalidadEstudio pre = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        ModalidadEstudio epg = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.EPG);

        List<SituacionAcademica> situacionesPregrado = situacionAcademicaDAO.allByCodes(
                Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_8, S_9, S_EM, S_3U, S_2U, S_4U, S_6U, S_TU));
        List<SituacionAcademica> situacionesPosgrado = situacionAcademicaDAO.allByCodes(
                Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_EM));

        List<String> situacionesPre = situacionesPregrado.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        List<String> situacionesPos = situacionesPosgrado.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        logger.debug("Situaciones pregrado {}", String.join(",", situacionesPre));
        logger.debug("Situaciones posgrado {}", String.join(",", situacionesPos));

        List<Alumno> pregrados = alumnoDAO.allBySituaciones(pre, situacionesPregrado);
        List<Alumno> posgrados = alumnoDAO.allBySituaciones(epg, situacionesPosgrado);
        List<Alumno> unionList = new ArrayList();
        unionList.addAll(pregrados);
        unionList.addAll(posgrados);


        /*
        List<CicloAcademico> allCiclosActivesPre = cicloAcademicoDAO.allActivesByModalidad(pre, new String[]{"ca.year asc", "ca.numeroCiclo asc"});
        List<CicloAcademico> allCiclosActivesEpg = cicloAcademicoDAO.allActivesByModalidad(epg, new String[]{"ca.year asc", "ca.numeroCiclo asc"});
         */
        // visorCalculoNotas.setCantidadTotal(pregrados.size());
        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);

        List<AlumnoCicloCurso> allOperativesByModalidadEstudio = alumnoCicloCursoDAO.allOperativesByModalidadEstudio(ModalidadEstudioEnum.PRE);
        logger.debug("alumno ciclo curso operatives {}", allOperativesByModalidadEstudio.size());
        //   Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoByAlu = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", allOperativesByModalidadEstudio);
        visorCalculoNotas.iniciar();
        visorCalculoNotas.setCantidadTotal(pregrados.size());
        for (Alumno alumno : pregrados) {
            if (!alumno.getCarrera().getId().equals(6L)) {
                continue;
            }
            //    List<AlumnoCicloCurso> alumnosCicloCursoByAlumno = mapAlumnoCicloCursoByAlu.get(alumno.getId());
            promedioService.promediarAllCicloAsync(alumno, cicloActivo, allOperativesByModalidadEstudio, ds);
        }
        /*
        for (Alumno alumno : posgrados) {
            promedioService.promediarAllCicloAsync(alumno, ds.getUsuario());
        }
         */
        return "yeah";
    }

    @ResponseBody
    @RequestMapping("promediarAll/{alumno}")
    public String calcularAllPromediosByCiclo(HttpSession session, @PathVariable("alumno") Long alumnoId) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        //  List<CicloAcademico> allCiclosActivos = cicloAcademicoDAO.allActivesByModalidad(alumno.getModalidadEstudio(), new String[]{"ca.year asc", "ca.numeroCiclo asc"});
        visorCalculoNotas.iniciar();
        ds.setFechaAccionAudit(new Date());
        Alumno alumno = alumnoDAO.findAllInfo(alumnoId);
        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(alumno.getModalidadEstudio());
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        promedioService.promediarAllCicloSync(alumno, cicloActivo, alumnoCicloCursos, ds);

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
        //201700
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());

        List<CicloAcademico> ciclos = cicloAcademicoDAO.allWithInitAndOrderBy(2017, "ca.codigo asc", CicloAcademicoEstadoEnum.ACT, CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);

        for (CicloAcademico cicloAcademicoEach : ciclos) {
            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(new Alumno(alumnoId), cicloAcademicoEach);
            if (matriculaResumen == null) {
                continue;
            }
            List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByMatriculaResumenFull(matriculaResumen);
            if (matriculasCurso == null || matriculasCurso.isEmpty()) {
                continue;
            }
            List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allActivesByMatriculaResumen(Arrays.asList(matriculaResumen));
            visorCalculoNotas.iniciar();
            visorCalculoNotas.setCantidadTotal(1);
            logger.debug("##################Ciclo padre {} {} {}", cicloAcademicoEach.getId(), cicloAcademicoEach.getYear(), cicloAcademicoEach.getNumeroCiclo());
            promedioService.trasladarInformcionForHistorial(matriculaResumen, matriculasCurso, matriculaSeccions, ds, false);
        }
        return "yeah";
    }

}
