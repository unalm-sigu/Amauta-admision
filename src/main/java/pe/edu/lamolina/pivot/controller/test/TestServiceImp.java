package pe.edu.lamolina.pivot.controller.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCU;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.RetiroCurso;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.academico.calculonotas.CalculoNotasService;
import pe.edu.lamolina.pivot.controller.academico.promedio.ContadorComponent;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioReviewService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioSegundoService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCursoDAO;
import pe.edu.lamolina.pivot.dao.tramite.SerieDocumentoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class TestServiceImp implements TestService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SerieDocumentoDAO serieDocumentoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    EgresadoDAO egresadoDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    RetiroCicloDAO retiroCicloDAO;

    @Autowired
    RetiroCursoDAO retiroCursoDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    VisorCalculoNotas visorCalculoNotas;
    @Autowired
    CalculoNotasService calculoNotasService;
    @Autowired
    PromedioService promedioService;
    @Autowired
    PromedioReviewService promedioReviewService;
    @Autowired
    PromedioSegundoService promedioSegundoService;
    @Autowired
    ContadorComponent contadorComponent;
    @Autowired
    AvanceCurricularService avanceCurricularService;

    @Override
    @Transactional
    public void calcularAllResumenEvaluacion(Long seccionId, CicloAcademico ciclo, DataSessionPivot ds) {
        int loop = 1;

        Seccion seccionPV = seccionDAO.find(seccionId);

        List<MatriculaSeccion> alumnosSeccion = matriculaSeccionDAO.allMatriculadosByGpoSeccion(seccionPV.getGrupoSeccion());
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
    }

//    @Override
//    @Transactional
//    public void trasladarMatriculaCursoForPromedios(DataSessionPivot ds, Long alumnoId) {
//        Alumno alumno = alumnoDAO.find(new Alumno(alumnoId));
//        List<CicloAcademico> ciclos = cicloAcademicoDAO.allWithInitAndOrderBy(2017, "ca.codigo asc", CicloAcademicoEstadoEnum.ACT, CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);
//        ciclos.removeIf(x -> !x.getModalidadEstudio().equals(alumno.getModalidadEstudio()));
//        for (CicloAcademico cicloAcademicoEach : ciclos) {
//            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(new Alumno(alumnoId), cicloAcademicoEach);
//            if (matriculaResumen == null) {
//                continue;
//            }
//            List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByMatriculaResumenFull(matriculaResumen);
//            if (matriculasCurso.isEmpty()) {
//                continue;
//            }
//
//            List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allActivesByMatriculaResumen(Arrays.asList(matriculaResumen));
//            visorCalculoNotas.iniciar();
//            visorCalculoNotas.setCantidadTotal(1);
//            logger.debug("##################Ciclo padre {} {} {}", cicloAcademicoEach.getId(), cicloAcademicoEach.getYear(), cicloAcademicoEach.getNumeroCiclo());
//            //promedioService.actasNotasHaciaHistorial(matriculaResumen, matriculasCurso, matriculaSeccions, ds, false);
//        }
//    }
//    @Override
//    @Transactional
//    public void trasladarMatriculaCursoForPromedios(DataSessionPivot ds) {
//        List<CicloAcademico> ciclos = cicloAcademicoDAO.allWithInitAndOrderBy(2019, "ca.codigo asc", CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);
//        //   List<GrupoSeccion> gruposSeccionesByCiclo=gruposecc
//        for (CicloAcademico cicloAcademico : ciclos) {
//            List<MatriculaResumen> resumenesAll = matriculaResumenDAO.allMatriculadosByCiclo(cicloAcademico);
//            List<MatriculaCurso> cursosMatriculadosAll = matriculaCursoDAO.allMatriculadosByCiclo(cicloAcademico);
//            List<Alumno> alumnos = resumenesAll.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
//            List<AlumnoCicloCurso> cursosLlevadosAll = alumnoCicloCursoDAO.allByAlumnos(alumnos);
//
//            Map<Long, List<MatriculaCurso>> mapCursoMatriculado = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", cursosMatriculadosAll);
//            Map<Long, List<AlumnoCicloCurso>> mapCursoLlevado = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", cursosLlevadosAll);
//
//            visorCalculoNotas.iniciar();
//            visorCalculoNotas.setCantidadTotal(resumenesAll.size());
//            for (MatriculaResumen matriculaResumen : resumenesAll) {
//                Alumno alumno = matriculaResumen.getAlumno();
//                List<MatriculaCurso> cursosMatriculados = TypesUtil.getListNotNull(mapCursoMatriculado.get(alumno.getId()));
//                List<AlumnoCicloCurso> cursosLlevados = TypesUtil.getListNotNull(mapCursoLlevado.get(alumno.getId()));
//
//                //List<MatriculaSeccion> matriculasSeccion = TypesUtil.getListNotNull(mapMatriculaSeccion.get(matriculaResumen.getId()));
//                promedioService.actasNotasHaciaHistorial(matriculaResumen, cursosMatriculados, cursosLlevados, ds, false);
//            }
//        }
//    }
    @Async
    @Override
    @Transactional
    public void trasladarMatriculaCursoForPromediosCiclo(DataSessionPivot ds, String codigo, Long modalidad) {
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findByCodigoModalidadEstudio(codigo, new ModalidadEstudio(modalidad));
        //   List<GrupoSeccion> gruposSeccionesByCiclo=gruposecc
        List<MatriculaResumen> resumenesAll = matriculaResumenDAO.allMatriculadosByCiclo(cicloAcademico);
        List<MatriculaCurso> cursosMatriculadosAll = matriculaCursoDAO.allActivosByCiclo(cicloAcademico);
        List<Alumno> alumnos = resumenesAll.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<AlumnoCicloCurso> cursosLlevadosAll = alumnoCicloCursoDAO.allByAlumnos(alumnos);

        Map<Long, List<MatriculaCurso>> mapCursoMatriculado = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", cursosMatriculadosAll);
        Map<Long, List<AlumnoCicloCurso>> mapCursoLlevado = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", cursosLlevadosAll);

        String token = RandomStringUtils.randomAlphanumeric(43);
        visorCalculoNotas.createToken(token, alumnos);

        for (MatriculaResumen matriculaResumen : resumenesAll) {
            Alumno alumno = matriculaResumen.getAlumno();
            List<MatriculaCurso> cursosMatriculados = TypesUtil.getListNotNull(mapCursoMatriculado.get(alumno.getId()));
            List<AlumnoCicloCurso> cursosLlevados = TypesUtil.getListNotNull(mapCursoLlevado.get(alumno.getId()));

            //List<MatriculaSeccion> matriculasSeccion = TypesUtil.getListNotNull(mapMatriculaSeccion.get(matriculaResumen.getId()));
            promedioService.actasNotasHaciaHistorial(matriculaResumen, cursosMatriculados, cursosLlevados, ds, token);
        }

    }

    @Async
    @Override
    @Transactional
    public void promediarciclocod(String cicloCod, DataSessionPivot ds) {
        List<CicloAcademico> ciclosAll = cicloAcademicoDAO.all();
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allByCodigo(cicloCod);
        List<String> ciclosStr = ciclos.stream().map(x -> x.toString()).collect(Collectors.toList());
        logger.info("ciclos encontrados {}", String.join(",", String.join(",", ciclosStr)));

        List<CicloAcademico> ciclosActivos = cicloAcademicoDAO.allActivosAlModalidades();

        for (CicloAcademico cicloAcademico : ciclos) {
            List<AlumnoCiclo> alumnosCiclosByCiclo = alumnoCicloDAO.allWithSituacionByCiclo(cicloAcademico);
            List<Alumno> alumnos = alumnosCiclosByCiclo.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

            List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumnos(alumnos);
            List<AlumnoCicloCurso> alumnosCiclosCursosAll = alumnoCicloCursoDAO.allByAlumnos(alumnos);
            for (AlumnoCicloCurso aac : alumnosCiclosCursosActivos) {
                aac.getAlumnoCiclo().getAlumno().getId();
                aac.getAlumnoCiclo().getCicloAcademico().getId();
                aac.getCurso().getId();
            }
            Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursosActivos = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);
            Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursosAll = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosAll);

            List<AlumnoCiclo> alumnosCiclosAll = alumnoCicloDAO.allWithSituacionByAlumnos(alumnos);
            Map<Long, List<AlumnoCiclo>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumno.id", alumnosCiclosAll);

            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByEstadoTramiteAndAlumnos(alumnos, new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));
            Map<Long, List<Reincorporacion>> mapReincorporacion = TypesUtil.convertListToMapList("alumno.id", reincorporaciones);

            List<MatriculaResumen> matriculasResumen = matriculaResumenDAO.allByCicloFull(cicloAcademico);
            logger.info("matriculas resumen encontradas {}, del ciclo {}", matriculasResumen.size(), cicloAcademico.toString());

            List<Egresado> egresados = egresadoDAO.allByAlumnos(alumnos);
            Map<Long, Egresado> mapEgresado = TypesUtil.convertListToMap("alumno.id", egresados);

            for (MatriculaResumen mResumen : matriculasResumen) {
                Alumno alumno = mResumen.getAlumno();
                Egresado egresado = mapEgresado.get(alumno.getId());
                List<AlumnoCiclo> alumnoCiclos = TypesUtil.getListNotNull(mapAlumnoCiclo.get(alumno.getId()));
                List<AlumnoCicloCurso> alumnoCicloCursoActivoByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursosActivos.get(alumno.getId()));
                List<AlumnoCicloCurso> alumnoCicloCursoAllByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursosAll.get(alumno.getId()));
                List<Reincorporacion> reincorporacionesByAlumno = TypesUtil.getListNotNull(mapReincorporacion.get(alumno.getId()));

                CicloAcademico cicloActivoByModalidad = ciclosActivos.stream()
                        .filter(x -> x.getModalidadEstudio().getCodigoEnum().equals(alumno.getModalidadEstudio().getOperativeModalidadEnum()))
                        .findFirst().orElse(null);

                promedioService.promediarAllCicloAsync(
                        alumno,
                        cicloActivoByModalidad,
                        egresado,
                        ciclosAll,
                        alumnoCiclos,
                        alumnoCicloCursoActivoByAlu,
                        alumnoCicloCursoAllByAlu,
                        reincorporacionesByAlumno, ds, null, false, false);
            }

        }
    }

    @Override
    @Transactional
    public void promediarciclocoderror(String cicloCod, DataSessionPivot ds) {
        List<CicloAcademico> ciclosAll = cicloAcademicoDAO.all();
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allByCodigo(cicloCod);
        List<String> ciclosStr = ciclos.stream().map(x -> x.toString()).collect(Collectors.toList());
        logger.info("ciclos encontrados {}", String.join(",", String.join(",", ciclosStr)));

        List<CicloAcademico> ciclosActivos = cicloAcademicoDAO.allActivosAlModalidades();

        for (CicloAcademico cicloAcademico : ciclos) {
            List<AlumnoCiclo> alumnosCiclosByCiclo = alumnoCicloDAO.allWithSituacionErrorByCiclo(cicloAcademico);
            List<Alumno> alumnos = alumnosCiclosByCiclo.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

            List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumnos(alumnos);
            List<AlumnoCicloCurso> alumnosCiclosCursosAll = alumnoCicloCursoDAO.allByAlumnos(alumnos);
            for (AlumnoCicloCurso aac : alumnosCiclosCursosActivos) {
                aac.getAlumnoCiclo().getAlumno().getId();
                aac.getAlumnoCiclo().getCicloAcademico().getId();
                aac.getCurso().getId();
            }
            Map<Long, List<AlumnoCicloCurso>> mapAlumnosCiclosCursosActivos = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);
            Map<Long, List<AlumnoCicloCurso>> mapAlumnosCiclosCursosAll = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosAll);

            List<AlumnoCiclo> alumnosCiclosAll = alumnoCicloDAO.allWithSituacionByAlumnos(alumnos);
            Map<Long, List<AlumnoCiclo>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumno.id", alumnosCiclosAll);

            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByEstadoTramiteAndAlumnos(alumnos, new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));
            Map<Long, List<Reincorporacion>> mapReincorporacion = TypesUtil.convertListToMapList("alumno.id", reincorporaciones);

            List<MatriculaResumen> matriculasResumen = matriculaResumenDAO.allByCicloFull(cicloAcademico);
            logger.info("matriculas resumen encontradas {}, del ciclo {}", matriculasResumen.size(), cicloAcademico.toString());

            List<Egresado> egresados = egresadoDAO.allByAlumnos(alumnos);
            Map<Long, Egresado> mapEgresado = TypesUtil.convertListToMap("alumno.id", egresados);

            for (MatriculaResumen mResumen : matriculasResumen) {
                Alumno alumno = mResumen.getAlumno();
                Egresado egresado = mapEgresado.get(alumno.getId());
                List<AlumnoCiclo> alumnoCiclos = TypesUtil.getListNotNull(mapAlumnoCiclo.get(alumno.getId()));
                List<AlumnoCicloCurso> alumnoCicloCursoActivosByAlu = TypesUtil.getListNotNull(mapAlumnosCiclosCursosActivos.get(alumno.getId()));
                List<AlumnoCicloCurso> alumnoCicloCursoAllByAlu = TypesUtil.getListNotNull(mapAlumnosCiclosCursosAll.get(alumno.getId()));
                List<Reincorporacion> reincorporacionesByAlumno = TypesUtil.getListNotNull(mapReincorporacion.get(alumno.getId()));

                CicloAcademico cicloActivoByModalidad = ciclosActivos.stream()
                        .filter(x -> x.getModalidadEstudio().getCodigoEnum().equals(alumno.getModalidadEstudio().getOperativeModalidadEnum()))
                        .findFirst().orElse(null);
                promedioService.promediarAllCicloAsync(
                        alumno,
                        cicloActivoByModalidad,
                        egresado,
                        ciclosAll,
                        alumnoCiclos,
                        alumnoCicloCursoActivosByAlu,
                        alumnoCicloCursoAllByAlu,
                        reincorporacionesByAlumno, ds, null, false, false);
            }

        }
    }

    @Async
    @Override
    @Transactional
    public void promediarfull(DataSessionPivot ds, ModalidadEstudioEnum modalidadEnum) {
        List<String> allYears = alumnoDAO.allYearsCiclos();
        //List<String> allYears = Arrays.asList("2017");
        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();

        contadorComponent.iniciarTotal();
        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(modalidadEnum);
        for (String year : allYears) {
            List<Alumno> alumnos = alumnoDAO.allPendingPromedioByCicloYearAndModalidadEst(year, modalidadEnum);
            contadorComponent.iniciar(alumnos.size());
            logger.info("Año {}, Alumnos {}, Acumulados {}", year, alumnos.size(), contadorComponent.getCantidadAcumulada());

            promedioSegundoService.procesarYear(alumnos, cicloActivo, ciclos, ds);

            long t1 = System.currentTimeMillis();
            for (;;) {
                if (contadorComponent.finalizoProcesados()) {
                    break;
                }
                long t2 = System.currentTimeMillis();
                if (t2 - t1 > 5000) {
                    System.out.print(year + ": Ya se procesaron ");
                    System.out.print(contadorComponent.getProcesados() + " de " + contadorComponent.getMetaProcesados());
                    System.out.print(" - Acumulados " + contadorComponent.getCantidadAcumulada() + " alumnos ");
                    System.out.println("");
                    t1 = System.currentTimeMillis();
                }
            }
        }
        System.out.println("FIN-CALCULO-PROMEDIOS-" + modalidadEnum.name());
    }

    @Override
    @Transactional
    public void promediarAll(Long cicloId, DataSessionPivot ds) {

        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();
        CicloAcademico cicloAcademico = cicloAcademicoDAO.find(new CicloAcademico(cicloId));
        List<MatriculaResumen> matriculasResumen = matriculaResumenDAO.allByCicloFull(cicloAcademico);
        logger.info("matriculas resumen encontradas {}", matriculasResumen.size());

        List<Alumno> alumnos = matriculasResumen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<AlumnoCiclo> alumnosCiclosAll = alumnoCicloDAO.allByAlumnos(alumnos);
        Map<Long, List<AlumnoCiclo>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumno.id", alumnosCiclosAll);

        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumnos(alumnos);
        List<AlumnoCicloCurso> alumnosCiclosCursosAll = alumnoCicloCursoDAO.allByAlumnos(alumnos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoActivo = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoAll = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosAll);

        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByEstadoTramiteAndAlumnos(alumnos, new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));
        Map<Long, List<Reincorporacion>> mapReincorporacion = TypesUtil.convertListToMapList("alumno.id", reincorporaciones);

        List<Alumno> alumnosAllInfo = alumnoDAO.allInfoByAlumnos(alumnos);
        Map<Long, Alumno> mapAlumno = TypesUtil.convertListToMap("id", alumnosAllInfo);

        List<Egresado> egresados = egresadoDAO.allByAlumnos(alumnos);
        Map<Long, Egresado> mapEgresado = TypesUtil.convertListToMap("alumno.id", egresados);

        for (MatriculaResumen mResumen : matriculasResumen) {
            Alumno alumno = mapAlumno.get(mResumen.getAlumno().getId());
            Egresado egresado = mapEgresado.get(alumno.getId());
            List<AlumnoCiclo> alumnoCiclos = TypesUtil.getListNotNull(mapAlumnoCiclo.get(alumno.getId()));
//            List<AlumnoCicloCurso> alumnosCicloCursoByAlumno = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
            List<AlumnoCicloCurso> alumnoCiclosCursosActivosByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursoActivo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCiclosCursosAllByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursoAll.get(alumno.getId()));
            List<Reincorporacion> reincorporacionesByAlumno = TypesUtil.getListNotNull(mapReincorporacion.get(alumno.getId()));

            promedioService.promediarAllCicloAsync(
                    alumno,
                    cicloAcademico,
                    egresado,
                    ciclos,
                    alumnoCiclos,
                    alumnoCiclosCursosActivosByAlu,
                    alumnoCiclosCursosAllByAlu,
                    reincorporacionesByAlumno, ds, null, false, false);
        }
    }

//    @Override
//    @Transactional
//    public void calcularAllPromediosByCiclo(DataSessionPivot ds) {
//        CicloAcademico ciclo = ds.getCicloAcademico();
//
//        List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allHabilesByCiclo(ciclo);
//        logger.debug("Catidad de registros a procesar {}", matriculasCurso.size());
//        for (MatriculaCurso matriculaCurso : matriculasCurso) {
//            //   if (matriculaCurso.getMatriculaResumen().getAlumno().getId().compareTo(54234L) == 0) {
//
//            matriculaCurso.getMatriculaResumen().getAlumno();
//            matriculaCurso.getMatriculaResumen().getCicloAcademico();
//            matriculaCurso.getCurso();
//            promedioService.trasladoPromediosSource(matriculaCurso, ds, false);
//
//            //  }
//        }
//    }
    @Override
    public void promediarfullBySituacion(String sit, DataSessionPivot ds, ModalidadEstudioEnum modalidadEstudioEnum) {
        List<String> allYears = alumnoDAO.allYearsCiclos();
        List<CicloAcademico> ciclosAll = cicloAcademicoDAO.all();

        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(modalidadEstudioEnum);
        List<Alumno> alumnosAcumulados = new ArrayList();
        List<Egresado> egresadosAcumulados = new ArrayList();
        for (String year : allYears) {
            List<Alumno> alumnos = alumnoDAO.allPendingPromedioByCicloYearAndModalidadEst(year, modalidadEstudioEnum);
            alumnosAcumulados.addAll(alumnos);
            logger.info("Año {}, Alumnos {}, Acumulados {}", year, alumnos.size(), alumnosAcumulados.size());
            List<Egresado> egresados = egresadoDAO.allByAlumnos(alumnos);
            egresadosAcumulados.addAll(egresados);
        }

        List<AlumnoCiclo> alumnosCiclosAll = alumnoCicloDAO.allByAlumnos(alumnosAcumulados);
        Map<Long, List<AlumnoCiclo>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumno.id", alumnosCiclosAll);

        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumnos(alumnosAcumulados);
        List<AlumnoCicloCurso> alumnosCiclosCursosAll = alumnoCicloCursoDAO.allByAlumnos(alumnosAcumulados);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoActivo = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoAll = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosAll);

        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByEstadoTramiteAndAlumnos(alumnosAcumulados, new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));
        Map<Long, List<Reincorporacion>> mapReincorporacion = TypesUtil.convertListToMapList("alumno.id", reincorporaciones);

        Map<Long, Egresado> mapEgresado = TypesUtil.convertListToMap("alumno.id", egresadosAcumulados);

        contadorComponent.iniciar(alumnosAcumulados.size());
        for (Alumno alumno : alumnosAcumulados) {
            if (alumno.getSituacionAcademica().getCodigoEnum() != SituacionAcademicaEnum.get(sit)) {
                continue;
            }

            Egresado egresado = mapEgresado.get(alumno.getId());
            List<AlumnoCiclo> alumnoCiclos = TypesUtil.getListNotNull(mapAlumnoCiclo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCiclosCursosActivosByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursoActivo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCiclosCursosAllByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursoAll.get(alumno.getId()));
            List<Reincorporacion> reincorporacionesByAlumno = TypesUtil.getListNotNull(mapReincorporacion.get(alumno.getId()));

            promedioService.promediarAllCicloAsync(
                    alumno,
                    cicloActivo,
                    egresado,
                    ciclosAll,
                    alumnoCiclos,
                    alumnoCiclosCursosActivosByAlu,
                    alumnoCiclosCursosAllByAlu,
                    reincorporacionesByAlumno, ds, null, false, false);
        }
    }

    @Override
    public void trasladarMatriculaCursoForPromediosAlumno(DataSessionPivot ds, Long alumnoId) {
        Alumno alumno = alumnoDAO.find(new Alumno(alumnoId));
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allWithInitAndOrderBy(2017, "ca.codigo asc", CicloAcademicoEstadoEnum.ACT, CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);
        ciclos.removeIf(x -> !x.getModalidadEstudio().equals(alumno.getModalidadEstudio()));
        List<RetiroCiclo> retiroCiclos = retiroCicloDAO.allByRetiroCiclo(alumno);
        List<RetiroCurso> retiroCursos = retiroCursoDAO.allByAlumno(alumno);
        Map<Long, RetiroCiclo> mapRetiro = TypesUtil.convertListToMap("cicloAcademico.id", retiroCiclos);
        Map<String, RetiroCurso> mapRetiroCurso = TypesUtil.convertListToMap("key", retiroCursos);

        SituacionAcademica situacionAcademicaComodin = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_00.getValue());

        List<MatriculaResumen> matriculasResumenes = matriculaResumenDAO.allByAlumnoCiclos(alumno, ciclos);
        List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByMatriculaResumenFull(matriculasResumenes);
        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allByMatriculaResumenes(matriculasResumenes);

        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("cicloAcademico.id", matriculasResumenes);
        Map<Long, List<MatriculaCurso>> mapMatriculaCursoByMr = TypesUtil.convertListToMapList("matriculaResumen.id", matriculasCurso);
        Map<Long, List<MatriculaSeccion>> mapMatriculaSeccByMr = TypesUtil.convertListToMapList("matriculaResumen.id", matriculasSeccion);

        List<AlumnoCiclo> alumnosCiclos = alumnoCicloDAO.allByAlumno(alumno);
        Map<Long, AlumnoCiclo> mapAlumnoCiclos = TypesUtil.convertListToMap("cicloAcademico.id", alumnosCiclos);

        List<AlumnoCicloCurso> alumnoCicloCurso = alumnoCicloCursoDAO.allActivosByAlumno(alumno);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoByAlumCi = TypesUtil.convertListToMapList("alumnoCiclo.id", alumnoCicloCurso);

        for (CicloAcademico cicloAcademicoEach : ciclos) {
            MatriculaResumen matriculaResumen = mapMatriculaResumen.get(cicloAcademicoEach.getId());
            if (matriculaResumen == null) {
                continue;
            }
            List<MatriculaCurso> matriculaCursos = fillList(mapMatriculaCursoByMr.get(matriculaResumen.getId()));
            if (matriculaCursos == null || matriculaCursos.isEmpty()) {
                continue;
            }
            List<MatriculaSeccion> matriculaSeccions = fillList(mapMatriculaSeccByMr.get(matriculaResumen.getId()));

            this.trasladarMatriculaCursoForPromediosAlumnoTest(cicloAcademicoEach, mapRetiro.get(cicloAcademicoEach.getId()), retiroCursos, matriculaResumen, matriculaCursos, matriculaSeccions, ds);
        }
        promedioReviewService.trasladarInformcionForHistorial(matriculasResumenes, matriculasCurso, matriculasSeccion, ds, mapRetiro, mapAlumnoCicloCursoByAlumCi, mapAlumnoCiclos, situacionAcademicaComodin, false);
    }

    @Async
    @Override
    @Transactional
    public void trasladarMatriculaCursoForPromediosReview(DataSessionPivot ds, String codCiclo) {

        logger.debug("COMENZAMOOOOOOOOOOOO.........");
        logger.info("COMENZAMOOOOOOOOOOOO......... iNFO");
        logger.trace("COMENZAMOOOOOOOOOOOO......... TRACE");
        String codigo = codCiclo == null ? "201700" : codCiclo;
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allWithInitAndOrderBy(codigo, "ca.codigo asc", CicloAcademicoEstadoEnum.CER, CicloAcademicoEstadoEnum.PEND);
        logger.debug("Retirosssss...");
        List<RetiroCiclo> retirosCiclos = retiroCicloDAO.allInfo();
        Map<Long, List<RetiroCiclo>> mapAllRetiroByAlumno = TypesUtil.convertListToMapList("alumno.id", retirosCiclos);
        List<RetiroCurso> retirosCursos = retiroCursoDAO.allInfo();
        Map<Long, List<RetiroCurso>> mapRetiroCursoAlumno = TypesUtil.convertListToMapList("alumno.id", retirosCursos);

        for (CicloAcademico ciclo : ciclos) {
            List<CicloAcademico> ci = new ArrayList<>();
            ci.add(ciclo); //   List<GrupoSeccion> gruposSeccionesByCiclo=gruposecc
            //        Map<Long, List<RetiroCiclo>> mapRetiroByciclo = TypesUtil.convertListToMapList("cicloAcademico.id", retirosCiclos);
            //        Map<Long, List<RetiroCurso>> mapRetiroCursoByciclo = TypesUtil.convertListToMap("cicloAcademico.id", retirosCursos);
            List<MatriculaResumen> matriculasResumenes = matriculaResumenDAO.allByCiclos(ci);
            logger.debug("MAtricula Resumen...");
            List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByCiclosFull(ci);
            logger.debug("MAtricula Curso...");
            List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allByMatriculaResumenes(matriculasResumenes);
            logger.debug("MAtricula Seccion...");
//        Map<Long, List<MatriculaResumen>> mapMatriculaResumen = TypesUtil.convertListToMapList("cicloAcademico.id", matriculasResumenes);
            Map<Long, List<MatriculaResumen>> mapMatriculaResumenByAlumno = TypesUtil.convertListToMapList("alumno.id", matriculasResumenes);

            Map<Long, List<MatriculaCurso>> mapMatriculaCursoByMr = TypesUtil.convertListToMapList("matriculaResumen.id", matriculasCurso);
            Map<Long, List<MatriculaCurso>> allByAlumno = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", matriculasCurso);

            Map<Long, List<MatriculaSeccion>> mapMatriculaSeccByMr = TypesUtil.convertListToMapList("matriculaResumen.id", matriculasSeccion);
            Map<Long, List<MatriculaSeccion>> mapMatriculaSeccByAlumno = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", matriculasSeccion);

            List<AlumnoCiclo> alumnosCiclos = alumnoCicloDAO.allByCicloAcademicos(ci);
            logger.debug("Alumno Ciclo...");
            Map<Long, List<AlumnoCiclo>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumno.id", alumnosCiclos);

            List<AlumnoCicloCurso> alumnoCicloCurso = alumnoCicloCursoDAO.allByAlumnosCiclos(alumnosCiclos);
            logger.debug("Alumno Ciclo Curso...");
            Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoByAlumCi = TypesUtil.convertListToMapList("alumnoCiclo.id", alumnoCicloCurso);

            SituacionAcademica situacionAcademicaComodin = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_00.getValue());
            logger.debug("Situaciones ...");

            List<Alumno> alumnoos = matriculasResumenes.stream().map(x -> x.getAlumno()).distinct().collect(Collectors.toList());

            int i = 1;
            for (Alumno alumnoo : alumnoos) {
                logger.debug("Alumno {} {}", alumnoo.getCodigo(), alumnoo.getId());

                List<RetiroCiclo> allRetiroCicloAlumno = fillList(mapAllRetiroByAlumno.get(alumnoo.getId()));
                Map<Long, RetiroCiclo> mapRetiroByCicloAlumno = TypesUtil.convertListToMap("cicloAcademico.id", allRetiroCicloAlumno);
                List<RetiroCurso> retiroCursos = fillList(mapRetiroCursoAlumno.get(alumnoo.getId()));

                List<MatriculaResumen> matriculaResumens = mapMatriculaResumenByAlumno.get(alumnoo.getId());

                for (MatriculaResumen matriculaResumen : matriculaResumens) {
                    RetiroCiclo retiroCiclo = mapRetiroByCicloAlumno.get(matriculaResumen.getCicloAcademico().getId());
                    List<MatriculaCurso> matriculaCursos = fillList(mapMatriculaCursoByMr.get(matriculaResumen.getId()));
                    List<MatriculaSeccion> matriculaSeccions = fillList(mapMatriculaSeccByMr.get(matriculaResumen.getId()));
                    trasladarMatriculaCursoForPromediosAlumnoTest(matriculaResumen.getCicloAcademico(), retiroCiclo, retiroCursos, matriculaResumen, matriculaCursos, matriculaSeccions, ds);
                }
                List<MatriculaCurso> allMatriculasCursosAlumno = fillList(allByAlumno.get(alumnoo.getId()));
                List<MatriculaSeccion> matriculaSeccions = fillList(mapMatriculaSeccByAlumno.get(alumnoo.getId()));

                List<AlumnoCiclo> alumnoCiclos = fillList(mapAlumnoCiclo.get(alumnoo.getId()));
                if (alumnoCiclos.isEmpty()) {
                    continue;
                }
                Map<Long, AlumnoCiclo> mapAlumnoCicloByCiclo = TypesUtil.convertListToMap("cicloAcademico.id", alumnoCiclos);

                promedioReviewService.trasladarInformcionForHistorial(matriculaResumens, allMatriculasCursosAlumno, matriculaSeccions, ds, mapRetiroByCicloAlumno, mapAlumnoCicloCursoByAlumCi, mapAlumnoCicloByCiclo, situacionAcademicaComodin, false);

                logger.debug("avance {} de {}", i, alumnoos.size());
                i++;

            }
        }

    }

    private List fillList(List lista) {
        if (lista == null) {
            return new ArrayList();
        }
        return lista;
    }

    @Transactional
    private void trasladarMatriculaCursoForPromediosAlumnoTest(CicloAcademico cicloAcademico,
            RetiroCiclo retiroCiclo,
            List<RetiroCurso> retiroCursos,
            MatriculaResumen matriculaResumen, List<MatriculaCurso> matriculasCursoMat,
            List<MatriculaSeccion> matriculaSeccions,
            //            Map<Long, RetiroCiclo> mapRetiroCicloByciclo,
            //            Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso,
            //            Map<Long, AlumnoCiclo> mapAlumnoCicloByAlum,
            //            SituacionAcademica situacionAcademicaComodin,
            DataSessionPivot ds) {

        Map<String, RetiroCurso> mapRetiroCurso = TypesUtil.convertListToMap("key", retiroCursos);

        logger.debug("##################Ciclo padre {} {} {} alumno {} id {}", cicloAcademico.getId(), cicloAcademico.getYear(), cicloAcademico.getNumeroCiclo(), matriculaResumen.getAlumno().getCodigo(), matriculaResumen.getAlumno().getId());
        if (retiroCiclo != null) {
            matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.RCI);
            for (MatriculaCurso matriculaCurso : matriculasCursoMat) {
                MatriculaSeccion matriculaSeccion = matriculaSeccions
                        .stream().filter(x -> x.getSeccion().getGrupoSeccion().getCurso().getId().equals(matriculaCurso.getCurso().getId())).findFirst().orElse(null);
                if (RCI != matriculaSeccion.getEstadoEnum()) {

                    matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.RCI);
                    matriculaSeccionDAO.update(matriculaSeccion);
                }

                if (RCI != matriculaCurso.getEstadoEnum()) {

                    matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.RCI);
                    matriculaCursoDAO.updateColumns(matriculaCurso, "estado");
                }
            }
            if (RCI != matriculaResumen.getEstadoEnum()) {
                matriculaResumenDAO.update(matriculaResumen);
            }
        } else {

            for (MatriculaCurso matriculaCurso : matriculasCursoMat) {

                MatriculaSeccion matriculaSeccion = matriculaSeccions
                        .stream().filter(x -> x.getSeccion().getGrupoSeccion().getCurso().getId().equals(matriculaCurso.getCurso().getId())).findFirst().orElse(null);
                String keys = matriculaCurso.getCurso().getId() + "-" + cicloAcademico.getId();
                if (mapRetiroCurso.get(keys) != null) {
                    if (RCU != matriculaSeccion.getEstadoEnum()) {

                        matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.RCU);
                        matriculaSeccionDAO.update(matriculaSeccion);
                    }
                }

                if (mapRetiroCurso.get(keys) != null) {
                    if (RCU != matriculaCurso.getEstadoEnum()) {
                        matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.RCU);
                        matriculaCursoDAO.updateColumns(matriculaCurso, "estado");
                    }
                }
            }
        }
//        promedioReviewService.actasNotasHaciaHistorial(matriculaResumen, matriculasCursoMat, matriculaSeccions, ds, mapRetiroCicloByciclo, mapAlumnoCicloCurso, mapAlumnoCicloByAlum, situacionAcademicaComodin, false);
//        }
    }

    @Async
    @Override
    public void revisarCurriculasCiclo(String codigoCiclo, DataSessionPivot ds) {
        CicloAcademico ciclo = cicloAcademicoDAO.findByCodigoCicloModalidadEnum(codigoCiclo, ModalidadEstudioEnum.PRE);
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allHabilesByCiclo(ciclo);
        List<Alumno> alumnos = matriculables.stream().map(x -> x.getAlumno()).filter(x -> x.isPregrado()).collect(Collectors.toList());

        avanceCurricularService.generarAvanceCurricularByAlumnosPregrados(alumnos, ds, null);
    }

    @Async
    @Override
    public void revisarCurriculasCarrera(String codigoCarrera, DataSessionPivot ds) {
        List<Alumno> alumnos = alumnoDAO.allByPlanCarrera(codigoCarrera);
        avanceCurricularService.generarAvanceCurricularByAlumnosPregrados(alumnos, ds, null);
    }

}
