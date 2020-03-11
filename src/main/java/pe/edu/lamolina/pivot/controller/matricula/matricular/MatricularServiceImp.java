package pe.edu.lamolina.pivot.controller.matricula.matricular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NVAC;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RET;
import pe.edu.lamolina.model.enums.EstadoVacanteAlumnoEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.EEP;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELC;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELE;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.OBL;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.matricula.MatriculaSimultaneo;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.TurnoAtencionDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.model.Notificacion;

@Service
@Transactional(readOnly = true)
public class MatricularServiceImp implements MatricularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TurnoAtencionDAO turnoAtencionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSimultaneoDAO matriculaSimultaneoDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    VacanteService vacanteService;

    @Override
    public TurnoAtencion findTurnoAtencion(Long turnoAtencion) {
        return turnoAtencionDAO.findById(turnoAtencion);
    }

    @Override
    @Transactional
    public void matricular(TurnoAtencion turnoAtencionForm, DataSessionPivot ds) {
        matricularBarrido(turnoAtencionForm, ds);

    }

    private void matricularBarrido(TurnoAtencion turnoAtencionForm, DataSessionPivot ds) {
        logger.debug("**init matricula**");

        Notificacion notify = new Notificacion();

        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        Date date = new Date();
        TurnoAtencion turnoAtencion = turnoAtencionDAO.findById(turnoAtencionForm.getId());
        boolean beetwen = date.after(turnoAtencion.getFechaHoraEspera()) && date.before(turnoAtencion.getFechaHoraFin());

        logger.debug("date {} inicio {} fin {} ",
                new DateTime(date).toString("dd/MM/yyyy HH:mm"),
                new DateTime(turnoAtencion.getFechaHoraEspera()).toString("dd/MM/yyyy HH:mm"),
                new DateTime(turnoAtencion.getFechaHoraFin()).toString("dd/MM/yyyy HH:mm")
        );

//        if (!beetwen) {
//            throw new PhobosException("No está dentro del horario de matrícula");
//        }
        long t1 = System.currentTimeMillis();
        List<MatriculaResumen> resumenes = matriculaResumenDAO.allByCicloMat(cicloAcademico);
        Map<Long, MatriculaResumen> mapResumenes = TypesUtil.convertListToMap("id", resumenes);

        List<MatriculaCurso> matriculaCursosNoSim = matriculaCursoDAO.allByMatriculaResumenes(resumenes);
        for (MatriculaCurso mc : matriculaCursosNoSim) {
            MatriculaResumen mr = mapResumenes.get(mc.getMatriculaResumen().getId());
            mc.setMatriculaResumen(mr);
        }

        List<Curso> cursosNoSim = matriculaCursosNoSim.stream().map(x -> x.getCurso()).distinct().collect(Collectors.toList());
        Map<Long, List< MatriculaCurso>> mapMatResumenNoSim = TypesUtil.convertListToMapList("matriculaResumen.id", matriculaCursosNoSim);

        List<MatriculaSimultaneo> matriculaSim = matriculaSimultaneoDAO.allByMatriculaResumen(resumenes);
        for (MatriculaSimultaneo mSim : matriculaSim) {
            MatriculaResumen mr = mapResumenes.get(mSim.getMatriculaCurso().getMatriculaResumen().getId());
            mSim.getMatriculaCurso().setMatriculaResumen(mr);
        }

        List<Curso> cursosSim = matriculaSim.stream().map(x -> x.getMatriculaCurso().getCurso()).distinct().collect(Collectors.toList());
        cursosNoSim.addAll(cursosSim);
        Map<Long, List< MatriculaCurso>> mapMatResumenSim = TypesUtil.convertListToMapList("matriculaCurso.matriculaResumen.id", "matriculaCurso", matriculaSim);

        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allByMatriculaResumenes(resumenes, cicloAcademico);
        for (MatriculaSeccion ms : matriculaSeccions) {
            MatriculaResumen mr = mapResumenes.get(ms.getMatriculaResumen().getId());
            ms.setMatriculaResumen(mr);
        }
        List<Seccion> seccions = matriculaSeccions.stream().map(x -> x.getSeccion()).distinct().collect(Collectors.toList());

        List<VacanteAlumno> vacanteAlumnos = vacanteAlumnoDAO.allActivoBySecciones(seccions);
        Map<Long, List<VacanteAlumno>> vacanteAlumnosMap = TypesUtil.convertListToMapList("seccion.id", vacanteAlumnos);

        cursosNoSim = cursosNoSim.stream().distinct().collect(Collectors.toList());
        notify.setTotalCurso(cursosNoSim.size());
        notify.setTotalSeccion(seccions.size());

        Collections.sort(resumenes, new MatriculaResumen.ComparePrioridadAsc());
        List<MatriculaResumen> listNoMatriculados = new ArrayList<>();
        List<MatriculaResumen> listMatriculados = new ArrayList<>();
        List<Alumno> alumnos = resumenes.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnos(alumnos);
        Map<Long, Integer> mapVacantesDisponibles = TypesUtil.convertListToMap("id", "vacantesDisponibles", seccions);
        Map<Long, Integer> mapMatriculadosSeccion = TypesUtil.convertListToMap("id", "matriculados", seccions);

        long t2 = System.currentTimeMillis();
        logger.debug("load data en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        procesarData(OBL,
                resumenes,
                notify,
                mapMatResumenNoSim,
                mapMatResumenSim,
                matriculaSeccions,
                mapVacantesDisponibles,
                vacanteAlumnosMap,
                alumnoCursoCurriculas,
                matriculaSim,
                mapMatriculadosSeccion,
                listMatriculados,
                listNoMatriculados,
                ds);
        t2 = System.currentTimeMillis();
        logger.debug("procesarData OBL en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        procesarData(EEP,
                resumenes,
                notify,
                mapMatResumenNoSim,
                mapMatResumenSim,
                matriculaSeccions,
                mapVacantesDisponibles,
                vacanteAlumnosMap,
                alumnoCursoCurriculas,
                matriculaSim,
                mapMatriculadosSeccion,
                listMatriculados,
                listNoMatriculados,
                ds);
        t2 = System.currentTimeMillis();
        logger.debug("procesarData EEP en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        int loop = 0;
        for (Map.Entry<Long, Integer> entry : mapMatriculadosSeccion.entrySet()) {
            Seccion seccion = new Seccion();
            seccion.setId(entry.getKey());
            seccion.setMatriculados(entry.getValue());
            vacanteService.enviarSeccion(seccion);
            //seccionDAO.updateMatriculados(seccion);
            loop++;
            logger.debug("actualizando seccion {} - {} de {}", seccion.getCodigo2(), loop, mapMatriculadosSeccion.entrySet().size());
        }
        t2 = System.currentTimeMillis();
        logger.debug("seccionDAO.updateMatriculados en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        loop = 0;
        for (MatriculaResumen matri : listMatriculados) {
            if (matri.getCursosMatriculados() == 0) {
                matri.setEstado(EstadoMatriculaEnum.NMAT.name());
            }
            vacanteService.enviarMatriculaResuemn(matri);
            //matriculaResumenDAO.updateCreditos(matri);

            loop++;
            logger.debug("actualizando mat-resumen MAT {} - {} de {}", matri.getId(), loop, listMatriculados.size());
        }
        t2 = System.currentTimeMillis();
        logger.debug("matriculaResumenDAO.updateCreditos en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        loop = 0;
        listNoMatriculados = listNoMatriculados.stream().distinct().collect(Collectors.toList());
        for (MatriculaResumen noMatri : listNoMatriculados) {
            if (noMatri.getCursosMatriculados() == 0) {
                noMatri.setEstado(EstadoMatriculaEnum.NMAT.name());
                //matriculaResumenDAO.updateCreditos(noMatri);
                vacanteService.enviarMatriculaResuemn(noMatri);
            }
            loop++;
            logger.debug("actualizando mat-resumen NMAT {} - {} de {}", noMatri.getId(), loop, listNoMatriculados.size());
        }
        t2 = System.currentTimeMillis();
        logger.debug("matriculaResumenDAO.updateCreditos en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        loop = 0;
        TurnoAtencion lastTurnoAtencionByConfig = turnoAtencionDAO.findLastByConfiguracion(turnoAtencion.getConfiguracionTurnosAtencion());
        if (lastTurnoAtencionByConfig.getId().compareTo(turnoAtencion.getId()) == 0) {
            logger.debug("Este turno es el ultimo de su configuracion");
//            cicloAcademico.setFechaTurnosAsignados(null);
//            cicloAcademico.setFechaTurnosDisponibles(null);
//            cicloAcademicoDAO.updateFechasTurnosAignadosDisponibles(cicloAcademico);
        }
        t2 = System.currentTimeMillis();
        logger.debug("cicloAcademicoDAO.updateFechasTurnosAignadosDisponibles en {} mseg", (t2 - t1));
    }

    public void procesarData(
            TipoCursoCurriculaEnum cursoCurriculaEnum,
            List<MatriculaResumen> resumenes,
            Notificacion notify,
            Map<Long, List< MatriculaCurso>> mapMatResumenNoSim,
            Map<Long, List< MatriculaCurso>> mapMatResumenSim,
            List<MatriculaSeccion> matriculaSeccions,
            Map<Long, Integer> mapVacantesDisponibles,
            Map<Long, List<VacanteAlumno>> vacanteAlumnosMap,
            List<AlumnoCursoCurricula> alumnoCursoCurriculas,
            List<MatriculaSimultaneo> matriculaSim,
            Map<Long, Integer> mapMatriculadosSeccion,
            List<MatriculaResumen> listMatriculados,
            List<MatriculaResumen> listNoMatriculados,
            DataSessionPivot ds) {

        List<MatriculaCurso> noMatriculadosELC = new ArrayList();
        List<MatriculaCurso> matriculaCursosFiltrados = new ArrayList();
        List<MatriculaCurso> matriculaCursosModidicados = new ArrayList();

        List<VacanteAlumno> vacantesAlumnoTemp = new ArrayList();
        List<MatriculaSeccion> matriculaSeccionMatTemp = new ArrayList();
        List<MatriculaSeccion> matriculaSeccionNvacTemp = new ArrayList();
        List<MatriculaCurso> matriculaCursoMatriculados = new ArrayList();

        logger.debug("inicianando barrido. de {} alumnos", resumenes.size());
        long t0 = System.currentTimeMillis();
        int loop = 0;
        for (MatriculaResumen resumene : resumenes) {
            List<MatriculaCurso> matriculaCursosNoSim = TypesUtil.getListNotNull(mapMatResumenNoSim.get(resumene.getId()));
            List<MatriculaCurso> matriculaCursoSim = TypesUtil.getListNotNull(mapMatResumenSim.get(resumene.getId()));
            //logger.debug("matriculaCursosNoSim.size={} matriculaCursoSim,size={}", matriculaCursosNoSim.size(), matriculaCursoSim.size());

            matriculaCursosNoSim = matriculaCursosNoSim == null ? new ArrayList() : matriculaCursosNoSim;
            matriculaCursosFiltrados = data(cursoCurriculaEnum, matriculaCursosNoSim, noMatriculadosELC, matriculaSeccions);

            long t1 = System.currentTimeMillis();
            matObligatorios(
                    matriculaCursosFiltrados,
                    noMatriculadosELC,
                    mapVacantesDisponibles,
                    vacanteAlumnosMap,
                    alumnoCursoCurriculas,
                    vacantesAlumnoTemp,
                    matriculaSeccionMatTemp,
                    matriculaSeccionNvacTemp,
                    notify,
                    matriculaCursoMatriculados,
                    mapMatriculadosSeccion,
                    listMatriculados,
                    listNoMatriculados,
                    ds);

            matriculaCursosModidicados.addAll(matriculaCursosFiltrados);

            long t2 = System.currentTimeMillis();
            logger.debug("\tmatObligatorios en {} mseg", (t2 - t1));
            matriculaCursoSim = matriculaCursoSim == null ? new ArrayList() : matriculaCursoSim;
            matriculaCursosFiltrados = data(cursoCurriculaEnum, matriculaCursoSim, noMatriculadosELC, matriculaSeccions);
            if (!matriculaCursosFiltrados.isEmpty()) {

                long t3 = System.currentTimeMillis();
                matSimObligatorios(matriculaCursosFiltrados,
                        noMatriculadosELC,
                        matriculaSim,
                        matriculaCursoMatriculados,
                        mapVacantesDisponibles,
                        vacanteAlumnosMap,
                        alumnoCursoCurriculas,
                        notify,
                        matriculaSeccionMatTemp,
                        matriculaSeccionNvacTemp,
                        mapMatriculadosSeccion,
                        vacantesAlumnoTemp,
                        listMatriculados,
                        listNoMatriculados,
                        ds);
                matriculaCursosModidicados.addAll(matriculaCursosFiltrados);
                long t4 = System.currentTimeMillis();
                logger.debug("\tmatSimObligatorios en {} mseg", (t4 - t3));
            }

            long t5 = System.currentTimeMillis();
            loop++;
            logger.debug("\tfinalizo el alumno {} de {} en {} mseg - duraion actual de {}", loop, resumenes.size(), (t5 - t1), (t5 - t0));
        }

        for (MatriculaCurso mc : matriculaCursosModidicados) {
            MatriculaResumen mr = mc.getMatriculaResumen();
            logger.debug("{} mr.estado={}", mr.getId(), mr.getEstado());
        }
        logger.debug("vienen {} matCur", matriculaCursosModidicados.size());

        for (MatriculaCurso mcModi : matriculaCursoMatriculados) {
            matriculaCursoDAO.update(mcModi);
            List<MatriculaSeccion> matSeccs = mcModi.getMatriculaSeccion();
            for (MatriculaSeccion matSecc : matSeccs) {
                vacanteService.enviarMatriculaSeccion23(matSecc);
                //matriculaSeccionDAO.update(matSecc);
            }
        }

        for (MatriculaResumen resumene : resumenes) {
            List<MatriculaCurso> matriculaCursosNoSim = TypesUtil.getListNotNull(mapMatResumenNoSim.get(resumene.getId()));
            for (MatriculaCurso matriculaCurso : matriculaCursosNoSim) {
                matriculaCursoDAO.update(matriculaCurso);
            }
            List<MatriculaCurso> matriculaCursoSim = TypesUtil.getListNotNull(mapMatResumenSim.get(resumene.getId()));
            for (MatriculaCurso matriculaCurso : matriculaCursoSim) {
                vacanteService.enviarMatriculaCurso23(matriculaCurso);
                //matriculaCursoDAO.update(matriculaCurso);
            }
            //matriculaResumenDAO.update(resumene);
            vacanteService.enviarMatriculaResumen23(resumene);
        }

        if (!vacantesAlumnoTemp.isEmpty()) {
            //vacanteAlumnoDAO.updateEstado(vacantesAlumnoTemp);
            vacanteService.enviarVacanteAlumno(vacantesAlumnoTemp);
        }
        if (!matriculaSeccionMatTemp.isEmpty()) {
            //matriculaSeccionDAO.updateEstado(matriculaSeccionMatTemp, MAT);
            vacanteService.enviarMatSeccionEstado(matriculaSeccionMatTemp);
        }
        if (!matriculaSeccionNvacTemp.isEmpty()) {
            //matriculaSeccionDAO.updateEstado(matriculaSeccionNvacTemp, NVAC);
            vacanteService.enviarMatSeccionEstadoNVAC(matriculaSeccionNvacTemp);
        }

    }

    private List<MatriculaCurso> data(TipoCursoCurriculaEnum cursoCurriculaEnum,
            List<MatriculaCurso> matriculaCursos,
            List<MatriculaCurso> noMatriculadosELC,
            List<MatriculaSeccion> matriculaSeccions) {

        List<MatriculaCurso> matCursosOk = new ArrayList();
        for (MatriculaCurso matCur : matriculaCursos) {
            List<MatriculaSeccion> matriculaSeccion = matriculaSeccions.stream().
                    filter(x -> Objects.equals(x.getSeccion().getGrupoSeccion().getCurso().getId(), matCur.getCurso().getId())
                    && Objects.equals(x.getMatriculaResumen().getId(), matCur.getMatriculaResumen().getId())).collect(Collectors.toList());
            matCur.setMatriculaSeccion(matriculaSeccion);
            if (!matriculaSeccion.isEmpty()) {
                matCur.setGrupoSeccion(matriculaSeccion.get(0).getSeccion().getGrupoSeccion());
                matCursosOk.add(matCur);
            } else {
                matCur.setEstadoEnum(RET);
            }
        }

        Collections.sort(matCursosOk, new MatriculaCurso.CompareGrupoSeccion());
        ///  List<MatriculaCurso> matriculaCursosTemp = new ArrayList<>();
        if (cursoCurriculaEnum == OBL) {
            matCursosOk = matriculaCursos.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() != EEP).collect(Collectors.toList());
        } else {

            matCursosOk = matriculaCursos.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == EEP).collect(Collectors.toList());
            validadEEP(matCursosOk, noMatriculadosELC);
        }
        return matCursosOk;
    }

    private void validadEEP(List<MatriculaCurso> matriculaCursosTemp, List<MatriculaCurso> noMatriculadosELC) {
        for (MatriculaCurso matriculaCursoTemp : matriculaCursosTemp) {
            MatriculaResumen matriculaResumen = matriculaCursoTemp.getMatriculaResumen();
            List<MatriculaCurso> matriculaCursos = noMatriculadosELC.stream().filter(x
                    -> Objects.equals(x.getMatriculaResumen().getId(), matriculaResumen.getId())
                    && Arrays.asList(ELC, ELE).contains(x.getTipoCursoCurricula().getCodigoEnum())).collect(Collectors.toList());
            for (MatriculaCurso matriculaCurso : matriculaCursos) {
                if (matriculaCurso.getCreditos() <= matriculaCursoTemp.getCreditos()) {
                    matriculaCursoTemp.setTipoCursoCurricula(matriculaCursoTemp.getTipoCursoCurricula());
                    noMatriculadosELC.remove(matriculaCurso);
                    break;
                }
            }
        }
    }

    private void matObligatorios(
            List<MatriculaCurso> matriculaCursosTemp,
            List<MatriculaCurso> noMatriculadosELC,
            Map<Long, Integer> mapVacantesDisponibles,
            Map<Long, List<VacanteAlumno>> vacanteAlumnosMap,
            List<AlumnoCursoCurricula> alumnoCursoCurriculas,
            List<VacanteAlumno> vacantesAlumnoTemp,
            List<MatriculaSeccion> matriculaSeccionMatTemp,
            List<MatriculaSeccion> matriculaSeccionNvacTemp,
            Notificacion notify,
            List<MatriculaCurso> matriculaCursoMatriculados,
            Map<Long, Integer> mapMatriculadosSeccion,
            List<MatriculaResumen> listMatriculados,
            List<MatriculaResumen> listNoMatriculados,
            DataSessionPivot ds
    ) {
        Long idMatCurso = -1l;
        //logger.debug("va ser procesado {} mat-cursos", matriculaCursosTemp.size());
        for (MatriculaCurso matriculaCurso : matriculaCursosTemp) {
            //grupoSeccionDAO.findLock(matriculaCurso.getGrupoSeccion().getId());
            Curso curso = matriculaCurso.getCurso();
            MatriculaResumen mr = matriculaCurso.getMatriculaResumen();
            Alumno alumno = mr.getAlumno();

            for (MatriculaSeccion matriculaSeccion : matriculaCurso.getMatriculaSeccion()) {
                Boolean cumple = false;
                Seccion seccion = matriculaSeccion.getSeccion();
                if (seccion.getIsTipoSeccionTCUR()) {
                    continue;
                }
                Integer vac = mapVacantesDisponibles.get(seccion.getId());

                cumple = vac >= 1;
                matricularAll(cumple,
                        vacanteAlumnosMap,
                        seccion,
                        matriculaCurso,
                        alumnoCursoCurriculas,
                        alumno,
                        curso,
                        notify,
                        matriculaSeccionMatTemp,
                        matriculaSeccionNvacTemp,
                        mapMatriculadosSeccion,
                        mr,
                        mapVacantesDisponibles,
                        matriculaCursoMatriculados,
                        noMatriculadosELC,
                        matriculaSeccion,
                        vac,
                        vacantesAlumnoTemp,
                        listMatriculados,
                        listNoMatriculados,
                        ds);
                if (cumple && !Objects.equals(idMatCurso, matriculaCurso.getId())) {
                    //logger.debug("Cumple oblig");
//                    logger.debug("Entre.....");
                    mr.setCursosMatriculados(mr.getCursosMatriculados() + 1);
                    mr.setEstadoEnum(MAT);
                    mr.setCreditosMatriculados(curso.getCreditos() + mr.getCreditosMatriculados());
                    listMatriculados.add(mr);
                    idMatCurso = matriculaCurso.getId();
                } else {
                    //logger.debug("No cumple -oblig");
                }
            }
        }

    }

    private void matSimObligatorios(
            List<MatriculaCurso> matriculaCursoTemp,
            List<MatriculaCurso> noMatriculadosELC,
            List<MatriculaSimultaneo> matriculaCursosSim,
            List<MatriculaCurso> matriculaCursoMatriculados,
            Map<Long, Integer> mapVacantesDisponibles,
            Map<Long, List<VacanteAlumno>> vacanteAlumnosMap,
            List<AlumnoCursoCurricula> alumnoCursoCurriculas,
            Notificacion notify,
            List<MatriculaSeccion> matriculaSeccionMatTemp,
            List<MatriculaSeccion> matriculaSeccionNvacTemp,
            Map<Long, Integer> mapMatriculadosSeccion,
            List<VacanteAlumno> vacantesAlumnoTemp,
            List<MatriculaResumen> listMatriculados,
            List<MatriculaResumen> listNoMatriculados,
            DataSessionPivot ds
    ) {
        Map<Long, MatriculaCurso> map = TypesUtil.convertListToMap("matriculaCurso.id", "matriculaCursoSimultaneo", matriculaCursosSim);
        Long idMatCurso = -1l;
        for (MatriculaCurso matriculaCurso : matriculaCursoTemp) {
            //grupoSeccionDAO.findLock(matriculaCurso.getGrupoSeccion().getId());
            try {

                Curso curso = matriculaCurso.getCurso();
                MatriculaResumen mr = matriculaCurso.getMatriculaResumen();
                Alumno alumno = mr.getAlumno();
                MatriculaCurso requisitoSim = map.get(matriculaCurso.getId());
                Boolean cumple = matriculaCursoMatriculados.stream().anyMatch(x
                        -> Objects.equals(x.getCurso().getId(), requisitoSim.getCurso().getId())
                        && Objects.equals(x.getMatriculaResumen().getId(), matriculaCurso.getMatriculaResumen().getId()));
                for (MatriculaSeccion matriculaSeccion : matriculaCurso.getMatriculaSeccion()) {
                    Seccion seccion = matriculaSeccion.getSeccion();
                    if (seccion.getIsTipoSeccionTCUR()) {
                        continue;
                    }
                    Integer vac = mapVacantesDisponibles.get(seccion.getId());
                    cumple = !cumple ? cumple : vac >= 1;

                    matricularAll(cumple,
                            vacanteAlumnosMap,
                            seccion,
                            matriculaCurso,
                            alumnoCursoCurriculas,
                            alumno,
                            curso,
                            notify,
                            matriculaSeccionMatTemp,
                            matriculaSeccionNvacTemp,
                            mapMatriculadosSeccion,
                            mr,
                            mapVacantesDisponibles,
                            matriculaCursoMatriculados,
                            noMatriculadosELC,
                            matriculaSeccion,
                            vac,
                            vacantesAlumnoTemp,
                            listMatriculados,
                            listNoMatriculados,
                            ds);
                    if (cumple && !Objects.equals(idMatCurso, matriculaCurso.getId())) {
                        //logger.debug("Cumple sim-oblig");
                        mr.setCursosMatriculados(mr.getCursosMatriculados() + 1);
                        mr.setEstadoEnum(MAT);
                        mr.setCreditosMatriculados(curso.getCreditos() + mr.getCreditosMatriculados());
                        listMatriculados.add(mr);
                        idMatCurso = matriculaCurso.getId();
                    } else {
                        //logger.debug("No cumple sim-oblig");
                    }
                }
            } catch (Exception e) {
            }
        }

    }

    private void matricularAll(Boolean cumple,
            Map<Long, List<VacanteAlumno>> vacanteAlumnosMap,
            Seccion seccion,
            MatriculaCurso matriculaCurso,
            List<AlumnoCursoCurricula> alumnoCursoCurriculas,
            Alumno alumno,
            Curso curso,
            Notificacion notify,
            List<MatriculaSeccion> matriculaSeccionMatTemp,
            List<MatriculaSeccion> matriculaSeccionNvacTemp,
            Map<Long, Integer> mapMatriculadosSeccion,
            MatriculaResumen mr,
            Map<Long, Integer> mapVacantesDisponibles,
            List<MatriculaCurso> matriculaCursoMatriculados,
            List<MatriculaCurso> noMatriculadosELC,
            MatriculaSeccion matriculaSeccion,
            Integer vac,
            List<VacanteAlumno> vacantesAlumnoTemp,
            List<MatriculaResumen> listMatriculados,
            List<MatriculaResumen> listNoMatriculados,
            DataSessionPivot ds
    ) {
        if (cumple) {
            //VacanteAlumno vacante = this.getVacanteAlumno(vacanteAlumnosMap, seccion, ds.getUsuario());
//            logger.debug("alumno {}", alumno.getId());
//            logger.debug("seccion {}", seccion.getId());

            if (matriculaCurso.getEstadoEnum() != EstadoMatriculaEnum.MAT && !seccion.getIsTipoSeccionTCUR()) {
                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
                AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoCurriculas.stream().
                        filter(x -> Objects.equals(x.getAlumno().getId(), alumno.getId())
                        && Objects.equals(x.getCurso().getId(), curso.getId())
                        ).findAny().orElse(null);
                this.actualizarAlumnoCursoCurricula(EstadoMatriculaEnum.MAT, alumnoCursoCurricula);
                matriculaCurso.setFechaMatricula(new Date());
                matriculaCurso.setUserMatricula(ds.getUsuario());
                //matriculaCursoDAO.update(matriculaCurso);
                matriculaCursoMatriculados.add(matriculaCurso);

            }

            Integer matriculadosSeccion = mapMatriculadosSeccion.get(seccion.getId());
            matriculadosSeccion += 1;
            mapMatriculadosSeccion.replace(seccion.getId(), matriculadosSeccion);
            if (matriculaSeccion != null) {

                matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
                matriculaSeccionMatTemp.add(matriculaSeccion);
            }

//            vacante.setAlumno(alumno);
//            vacante.setEstadoEnum(EstadoVacanteAlumnoEnum.OCUP);
//            vacantesAlumnoTemp.add(vacante);
            vac -= 1;
            mapVacantesDisponibles.replace(seccion.getId(), vac);

            if (seccion.getIsTipoSeccionPCUR()) {
                vac = mapVacantesDisponibles.get(seccion.getSeccionSuperior().getId());
                MatriculaSeccion ms = matriculaCurso.getMatriculaSeccion().stream().filter(x -> Objects.equals(x.getSeccion().getId(), seccion.getSeccionSuperior().getId())).findAny().orElse(null);
                matricularAll(cumple, vacanteAlumnosMap,
                        seccion.getSeccionSuperior(), matriculaCurso, alumnoCursoCurriculas,
                        alumno, curso, notify, matriculaSeccionMatTemp, matriculaSeccionNvacTemp,
                        mapMatriculadosSeccion, mr, mapVacantesDisponibles, matriculaCursoMatriculados,
                        noMatriculadosELC, ms, vac, vacantesAlumnoTemp, listMatriculados, listNoMatriculados, ds);
            }
        } else {
            matriculaCurso.setEstadoEnum(NVAC);
            //matriculaCursoDAO.update(matriculaCurso);
            notify.setCurrentSeccion(notify.getCurrentSeccion() + 1);
            StringBuilder sd = new StringBuilder();
            sd.append("alumno ");
            sd.append(alumno.getCodigo());
            sd.append(" no hay vacante disponible en la clave ");
            sd.append(seccion.getCodigo2());
            notify.setMessage(sd.toString());
            notify.setState(false);
            this.actualizarAlumnoCursoCurricula(alumno, curso, CursoCurriculaEstadoEnum.HAB);
            this.notify(notify, ds.getUsuario());
            matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.NVAC);
            matriculaSeccionNvacTemp.add(matriculaSeccion);
            noMatriculadosELC.add(matriculaCurso);
            listNoMatriculados.add(mr);
            if (seccion.getIsTipoSeccionPCUR()) {
                MatriculaSeccion ms = matriculaCurso.getMatriculaSeccion().stream().filter(x -> Objects.equals(x.getSeccion().getId(), seccion.getSeccionSuperior().getId())).findAny().orElse(null);
                matricularAll(cumple, vacanteAlumnosMap,
                        seccion.getSeccionSuperior(), matriculaCurso,
                        alumnoCursoCurriculas, alumno, curso, notify, matriculaSeccionMatTemp,
                        matriculaSeccionNvacTemp, mapMatriculadosSeccion, mr, mapVacantesDisponibles,
                        matriculaCursoMatriculados, noMatriculadosELC, ms, vac, vacantesAlumnoTemp, listMatriculados, listNoMatriculados, ds);
            }
//                    matriculaSeccionDAO.update(matriculaSeccion);
        }
    }

    public void actualizarAlumnoCursoCurricula(Alumno alumno, Curso curso, CursoCurriculaEstadoEnum cursoCurriculaEstadoEnum) {
        if (true) {
            return;
        }
        AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoCurriculaDAO.findByAlumnoCurso(alumno, curso);
        AlumnoCursoCurricula alumnoCursoCurriculaUpd = new AlumnoCursoCurricula();
        alumnoCursoCurriculaUpd.setId(alumnoCursoCurricula.getId());
        alumnoCursoCurriculaUpd.setEstadoEnum(cursoCurriculaEstadoEnum);
        if (alumnoCursoCurricula.getEsSimultaneo()) {
            alumnoCursoCurriculaUpd.setEstadoEnum(CursoCurriculaEstadoEnum.SIM);
        }
        if (alumnoCursoCurricula.getTipoCursoCurriculaOrigen() != null) {
            alumnoCursoCurriculaUpd.setTipoCursoCurricula(alumnoCursoCurricula.getTipoCursoCurriculaOrigen());
        }
        alumnoCursoCurriculaDAO.updateEstado(alumnoCursoCurriculaUpd);
    }

    public void actualizarAlumnoCursoCurricula(EstadoMatriculaEnum estadoMatriculaEnum, AlumnoCursoCurricula alumnoCursoCurricula) {
        if (true) {
            return;
        }
        AlumnoCursoCurricula alumnoCursoCurriculaUpd = new AlumnoCursoCurricula();
        alumnoCursoCurriculaUpd.setId(alumnoCursoCurricula.getId());
        alumnoCursoCurriculaUpd.setEstadoMatriculaEnum(estadoMatriculaEnum);
        alumnoCursoCurriculaUpd.setEstadoEnum(alumnoCursoCurricula.getEstadoEnum());
        if (alumnoCursoCurricula.getEsSimultaneo()) {
            alumnoCursoCurriculaUpd.setEstadoEnum(CursoCurriculaEstadoEnum.SIM);
        }

        alumnoCursoCurriculaDAO.updateEstado(alumnoCursoCurriculaUpd);
    }

    @Override
    public Long countAllAlumnoPrematriculado(CicloAcademico cicloAcademico) {
        return matriculaCursoDAO.countAllAlumnoPrematriculado(cicloAcademico);
    }

    @Override
    public Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico) {
        return matriculaSeccionDAO.countAllSeccionPrematriculado(cicloAcademico);
    }

    private VacanteAlumno getVacanteAlumno(Map<Long, List<VacanteAlumno>> vacanteAlumnosMap, Seccion seccion, Usuario usuario) {
//        logger.debug("iniciando buequeda de vacante alumno");
        int vacantes = seccion.getVacantes();
//        logger.debug("cantidad de vacantes en la seccion {} {}", seccion.getId(), vacantes);
        List<VacanteAlumno> vacanteAlumnos = vacanteAlumnosMap.get(seccion.getId());
//        logger.debug("cantidad de vacantes en la seccion {} {}", seccion.getId(), vacanteAlumnos.size());
        if (vacanteAlumnos == null || vacanteAlumnos.isEmpty()) {
            vacanteAlumnos = new ArrayList();
            int cantidadVacantes = seccion.getVacantes();
            for (int i = 1; i <= cantidadVacantes; i++) {
                VacanteAlumno va = new VacanteAlumno();
                va.setNumero(i);
                va.setSeccion(seccion);
                va.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                va.setFechaRegistro(new Date());
                va.setUserRegistro(usuario);
                va.setActivo(1);
                vacanteAlumnoDAO.save(va);
                vacanteAlumnos.add(va);
            }
            vacanteAlumnosMap.put(seccion.getId(), vacanteAlumnos);
            return vacanteAlumnos.get(0);
        }
        int vacantesActuales = vacanteAlumnos.size();
        if (vacantesActuales < vacantes) {
            int delta = vacantes - vacanteAlumnos.size();
            for (int i = 1; i <= delta; i++) {
                VacanteAlumno va = new VacanteAlumno();
                va.setNumero(i + vacantesActuales);
                va.setSeccion(seccion);
                va.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                va.setFechaRegistro(new Date());
                va.setUserRegistro(usuario);
                va.setActivo(1);
                vacanteAlumnoDAO.save(va);
                vacanteAlumnos.add(va);
            }
            vacanteAlumnosMap.put(seccion.getId(), vacanteAlumnos);
        }
//        logger.debug("vacantes para las  seccion   {}  ", vacanteAlumnos.size());
        Map<Integer, VacanteAlumno> vacantesMap = TypesUtil.convertListToMap("numero", vacanteAlumnos);
//        logger.debug("vacantes para la  seccion after map  {} vacantes {} ", vacantesMap.size(), vacantes);
//        logger.debug("*******estado del map  key ");
//        for (Integer integer : vacantesMap.keySet()) {
//            logger.debug("*******estado del map key {}", vacantesMap.get(integer).getEstadoEnum().name());
//        }
        VacanteAlumno vacanteAlumno = null;
//        logger.debug("vacantes on  seccion   {} ", vacantes);
        for (int i = 1; i <= vacantes; i++) {
//            logger.debug("buscando la vacante     {} en un total de {}  ", i, vacantesMap.size());
            vacanteAlumno = vacantesMap.get(i);
//            logger.debug("vacanteAlumno   {} ", (vacanteAlumno != null));
            if (vacanteAlumno.getEstadoEnum() != EstadoVacanteAlumnoEnum.DISP) {
                vacanteAlumno = null;
            } else {
                break;
            }
        }
        return vacanteAlumno;
    }

    private void notify(Notificacion notify, Usuario usuario) {
        messagingTemplate.convertAndSendToUser(usuario.getGoogle(), "/monitoreo/notify", notify);
    }

}
